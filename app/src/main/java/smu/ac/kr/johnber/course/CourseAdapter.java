package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.network.ApiService;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RealmBasedRecyclerViewAdapter<RunningCourse, CourseViewHolder> implements Filterable {

    private final static String TAG = makeLogTag(CourseAdapter.class);
    private RealmResults<RunningCourse> data;
    private CourseViewHolder.itemClickListener listener;
    private Realm mRealm;
    private final String KEY = String.valueOf(R.string.google_maps_key);
    private GeoDataClient mGeoDataClient;

    public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                         boolean animateResults, CourseViewHolder.itemClickListener listener, GeoDataClient geoDataClient) {
        super(context, data, automaticUpdate, animateResults);
        this.data = data;
        this.listener = listener;
        this.mGeoDataClient = geoDataClient;
        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();
        LOGD(TAG,"API KEY : "+KEY);

    }

    @Override

    public CourseViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_item_collapsed, parent, false);
        CourseViewHolder viewHolder = new CourseViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindRealmViewHolder(CourseViewHolder holder, int position) {
        final RunningCourse courseItem = data.get(position);
        holder.courseName.setText(courseItem.getCourseName());
        holder.startPoint.setText(courseItem.getStartPoint());
        holder.distance.setText(courseItem.getDistance());
        holder.time.setText(courseItem.getTime());

        holder.KM.setText("KM");
        holder.TIME.setText("TIME");

//        //썸네일 다운로드 - google static maps API 사용
//
//        new PlaceRequest(getContext(), courseItem, holder, position,mGeoDataClient)
//                .findPlaceID(courseItem.getCourseName(),getLatLangFromAddr(courseItem).get(0));

        Uri uri = getStaticMapImg(courseItem);

        if (uri != null) {
            //placeholder : 이미지 로딩중 미리 보여지는 이미지
            Glide.with(getContext())
                    .load(uri)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_point_marker)
                            .error(R.drawable.ic_dust_testicon_replacelater)
                    )
                    .thumbnail(0.1f)
                    .into(holder.thumnail);
        }

    }

    @Override
    public int getItemCount() {
        LOGD(TAG, "item size :  " + data.size());
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                String charString = charSequence.toString();
                if (!charString.isEmpty()) {
                    RealmResults<RunningCourse> filteredList = mRealm.where(RunningCourse.class)
                            .contains("startPointRoadAddr", charString)
                            .or()
                            .contains("startPointAddr", charString)
                            .or()
                            .contains("endPointRoadAddr", charString)
                            .or()
                            .contains("endPointAddr", charString)
                            .or()
                            .contains("courseName", charString)
                            .findAll();
                    data = filteredList;
                    LOGD(TAG, "filtered result : " + data.size());
                }


                notifyDataSetChanged();
            }
        };
    }

    public Uri getStaticMapImg(RunningCourse course) {

        List<LatLng> list = getLatLangFromAddr(course);
        String staticURL = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=200x200" +
                "&markers=color:blue%7Clabel:S%7C"
                +list.get(0).latitude+","+list.get(0).longitude
                +"&markers=color:yellow%7Clabel:E%7C"
                +list.get(1).latitude+","+list.get(1).longitude +"&key="
                + ApiService.GOOGLE_MAPS_API_SERVICE_KEY;
//        LOGD(TAG, "static url "+course.getCourseName()+": "+staticURL);
        Uri uri = Uri.parse(staticURL);
      return uri;
    }

    private List<LatLng> getLatLangFromAddr(RunningCourse mcourseData) {
        List<LatLng> latlng = new ArrayList<>();
        if (mcourseData == null)
            LOGD(TAG, "mcourseData is empty");
        try {
            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
            Geocoder mGeoCoder = new Geocoder(getContext(), Locale.KOREA);
            List<Address> startLocation = null;
            List<Address> endLocation = null;
            if (!(mcourseData.getStartPointAddr().equals("null") || mcourseData.getStartPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
            } else if (!(mcourseData.getStartPointRoadAddr().equals("null") || mcourseData.getStartPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
            }

            if (!(mcourseData.getEndPointAddr().equals("null") || mcourseData.getEndPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());
                if (endLocation.size() <= 0) {
                    endLocation = startLocation;
                }
            } else if (!(mcourseData.getEndPointRoadAddr().equals("null") || mcourseData.getEndPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
            }
            LatLng start = new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude());
            LatLng end = new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude());
            latlng.add(start);
            latlng.add(end);
            LOGD(TAG, "size of lsitsts : " + latlng.size());


        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "cannot find location info " + mcourseData.getCourseName() + " " + mcourseData.getStartPointAddr() + " " + mcourseData.getStartPointRoadAddr() + " " + mcourseData.getEndPointAddr() + " " + mcourseData.getEndPointRoadAddr());
        }

        return latlng;
    }
}

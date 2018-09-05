package smu.ac.kr.johnber.course;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLng;

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

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

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
    private filteringlistener fListener;

    public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                         boolean animateResults, CourseViewHolder.itemClickListener listener, GeoDataClient geoDataClient, filteringlistener fListener) {
        super(context, data, automaticUpdate, animateResults);
        this.data = data;
        this.listener = listener;
        this.mGeoDataClient = geoDataClient;
        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();
        this.fListener = fListener;
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

        Uri uri = getStaticMapImg(courseItem);

        if (uri != null) {

            //placeholder : 이미지 로딩중 미리 보여지는 이미지
            Glide.with(getContext())
                    .load(uri)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_glide_placeholder)
                            .error(R.drawable.ic_glide_placeholder)
                    )
                    .thumbnail(0.9f)
                    .into(holder.thumnail);
        }

    }

    @Override
    public int getItemCount() {
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
                if (charString.equals("reset")) {
                    RealmResults<RunningCourse> filteredList = mRealm.where(RunningCourse.class).findAll();
                    data = filteredList;
                    updateRealmResults(filteredList);
                    LOGD(TAG, "filtered result : " + data.size());
                    notifyDataSetChanged();
                    fListener.onFiltered(filteredList);
                } else if (!charString.isEmpty()) {
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
                    updateRealmResults(filteredList);
                    LOGD(TAG, "filtered result : " + data.size());
                    notifyDataSetChanged();
                    fListener.onFiltered(filteredList);
                }

            }
        };
    }


    public Uri getStaticMapImg(RunningCourse course) {

        String staticURL = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=100x100" +
                "&markers=color:blue%7Clabel:S%7C"
                + course.getsLat() + "," + course.getsLng()
                + "&markers=color:yellow%7Clabel:E%7C"
                + course.geteLat() + "," + course.geteLng() + "&key="
                + ApiService.GOOGLE_MAPS_API_SERVICE_KEY;

        Uri uri = Uri.parse(staticURL);

        return uri;
    }

    public interface filteringlistener {
        public void onFiltered(RealmResults<RunningCourse> filteredResult);
    }


}

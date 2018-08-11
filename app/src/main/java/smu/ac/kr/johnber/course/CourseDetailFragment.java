package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import io.realm.Realm;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.run.RunningActivity;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.PermissionUtil;


/**
 * A simple {@link Fragment} subclass.
 */

//Todo : MAPVIEW setting
public class CourseDetailFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private final static String TAG = makeLogTag(CourseDetailFragment.class);
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private TextView courseName;
    public TextView startPoint;
    public TextView endPoint;
    public TextView distance;
    public TextView calories;
    public TextView time;
    public TextView course;
    public TextView courseInfo;
    private Realm mRealm;
    private int dataNO;
    private RunningCourse mcourseData;
    private View mView;
    private Marker mMarker;
    private MapView mMapView;
    private GoogleMap mgoogleMap;

    private Button mRun;
    private ScrollView scrollView;
    private FusedLocationProviderClient mFusedLocationClient;

    public CourseDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGD(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.course_detail_fragment, container, false);
        Realm.init(getActivity().getApplicationContext());
        mRealm = Realm.getDefaultInstance();
        dataNO = getArguments().getInt("position");
        courseName = mView.findViewById(R.id.tv_course_detail_summary_title);
        startPoint = mView.findViewById(R.id.tv_course_detail_summary_start_point);
        endPoint = mView.findViewById(R.id.tv_course_detail_summary_end_point);
        distance = mView.findViewById(R.id.tv_course_distance);
        calories = mView.findViewById(R.id.tv_course_calories);

        mRun = mView.findViewById(R.id.btn_course_run);  //TODO !!!!!달리기 실행으로 연결
        mRun.setOnClickListener(this);
        scrollView = mView.findViewById(R.id.scrollview);

        time = mView.findViewById(R.id.tv_course_time);
        course = mView.findViewById(R.id.tv_course_detail_info);
        courseInfo = mView.findViewById(R.id.tv_course_detail_info_content);
        mMapView = this.mView.findViewById(R.id.course_map_view);
        mcourseData = mRealm.where(RunningCourse.class).findAll().get(dataNO);
        courseName.setText(mcourseData.getCourseName());
        course.setText(mcourseData.getCourse());
        courseInfo.setText(mcourseData.getCourseInfo());
        startPoint.setText(mcourseData.getStartPoint());
        endPoint.setText(mcourseData.getEndPoint());
        time.setText(mcourseData.getTime());
        distance.setText(mcourseData.getDistance());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);



        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    mRun.setVisibility(View.GONE);
                } else if (scrollY <oldScrollY) {
                    mRun.setVisibility(View.VISIBLE);
                }

            }
        });



        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mgoogleMap = googleMap;

//    LOGD(TAG, "s "+mcourseData.getStartPointAddr());
//    LOGD(TAG, "s - "+mcourseData.getStartPointRoadAddr());
//    LOGD(TAG, "e - "+mcourseData.getEndPointAddr());
//    LOGD(TAG, "e - "+mcourseData.getEndPointRoadAddr());
        try {
            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
            Geocoder mGeoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.KOREA);
            List<Address> startLocation = null;
            List<Address> endLocation = null;
            ArrayList<LatLng> markerList = new ArrayList<>();
            if (!mcourseData.getStartPointAddr().equals("null")) {
                //지번주소
                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
            } else if (!mcourseData.getStartPointRoadAddr().equals("null")) {
                //도로명주소
                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
            }

            if (!mcourseData.getEndPointAddr().equals("null")) {
                //지번주소
                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());

            } else if (!mcourseData.getEndPointRoadAddr().equals("null")) {
                //도로명주소
                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
            }
            markerList.add(new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude()));
            markerList.add(new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude()));
            LOGD(TAG, "end : " + endLocation.get(0).getLatitude() + " " + endLocation.get(0).getLongitude());
            LOGD(TAG, "estar : " + startLocation.get(0).getLatitude() + " " + startLocation.get(0).getLongitude());
            LOGD(TAG, "size ; " + markerList.size());


            for (int position = 0; position < markerList.size(); position++) {

                mgoogleMap.addMarker(new MarkerOptions().position(markerList.get(position)).title(position + " ").icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, getActivity())));
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerList.get(0)).include(markerList.get(1));
            googleMap.setMinZoomPreference(13);
            LatLngBounds bounds = builder.build();
            mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "cannot find location info");
        }


    }

    //RUN 버튼 눌렀을 때
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if(!PermissionUtil.shouldAskPermission(getActivity(),PERMISSION)){
            //권한 있음
//            mRun.setVisibility(view.GONE);
//            RunningFragment runningFragment = new RunningFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.homeContainer, runningFragment, "RUNNINGFRAGMENT")
//                    .addToBackStack(null)
//                    .commit();

            //get the last known location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(),new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                Location mCurrentLocation = new Location(location.getProvider());
                                mCurrentLocation.setLatitude(location.getLatitude());
                                mCurrentLocation.setLongitude(location.getLongitude());
                                Intent intent = new Intent(getActivity(), RunningActivity.class);
                                //현재위치좌표를 같이 넘겨줌
                                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                                intent.putExtra("longitude", mCurrentLocation.getLongitude());
                                startActivity(intent);

                            }
                        }
                    });


        }else
            PermissionUtil.checkPermission(getActivity(),PERMISSION,REQUEST_LOCATION_PERMISSION);
    }


}

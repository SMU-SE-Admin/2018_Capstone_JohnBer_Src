package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import io.realm.Realm;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.util.BitmapUtil;


/**
 * A simple {@link Fragment} subclass.
 */

//Todo : MAPVIEW setting
public class CourseDetailFragment extends Fragment implements OnMapReadyCallback {
  private final static String TAG = makeLogTag(CourseDetailFragment.class);
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

  public CourseDetailFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
       Bundle savedInstanceState) {
    LOGD(TAG,"onCreateView");
     mView = inflater.inflate(R.layout.course_detail_fragment,container,false);

    Realm.init(getActivity().getApplicationContext());
    mRealm = Realm.getDefaultInstance();
    dataNO = getArguments().getInt("position");

    courseName = mView.findViewById(R.id.tv_course_detail_summary_title);
    startPoint = mView.findViewById(R.id.tv_course_detail_summary_start_point);
    endPoint = mView.findViewById(R.id.tv_course_detail_summary_end_point);
    distance = mView.findViewById(R.id.tv_course_distance);
    calories = mView.findViewById(R.id.tv_course_calories);
    time = mView.findViewById(R.id.tv_course_time);
    course = mView.findViewById(R.id.tv_course_detail_info);
    courseInfo= mView.findViewById(R.id.tv_course_detail_info_content);
    mMapView = this.mView.findViewById(R.id.course_map_view);
    mcourseData = mRealm.where(RunningCourse.class).findAll().get(dataNO);
    courseName.setText(mcourseData.getCourseName());
    course.setText(mcourseData.getCourse());
    courseInfo.setText(mcourseData.getCourseInfo());
    startPoint.setText(mcourseData.getStartPoint());
    LOGD(TAG, mcourseData.getStartPointRoadAddr());
    endPoint.setText(mcourseData.getEndPoint());
    time.setText(mcourseData.getTime());
    distance.setText(mcourseData.getDistance());
    mMapView.onCreate(savedInstanceState);
    mMapView.onResume();
    mMapView.getMapAsync(this);


    //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
    Geocoder mGeoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.KOREA);
    List<Address> startLocation;
//
//    try {
//      if(mcourseData.getStartPointAddr() != null) {
////        startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
//        startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(),1);
//        LOGD(TAG, "location "+
//                mcourseData.getStartPointRoadAddr());
//      } else{
//        startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(),1);
//        LOGD(TAG, "location "+
//                mcourseData.getStartPointRoadAddr());
//      }
//      double sLat = startLocation.get(0).getLatitude();
//      double sLng= startLocation.get(0).getLongitude();


//      List<Address> endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndpointAddr(),1);
//      markerList.add(new LatLng(endLocation.get(0).getLatitude(),endLocation.get(0).getLongitude()));
//
//      MarkerOptions options1 = new MarkerOptions();
//      options1.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_flag,getActivity()));
//      mgoogleMap.addMarker(options1);
//      mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng
//              (markerList.get(0)));
//    } catch (IOException e) {
//      e.printStackTrace();
//      LOGD(TAG,"cannot find location info");
//    }

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
    ArrayList<LatLng> markerList = new ArrayList<>();

    mgoogleMap = googleMap;
    googleMap.setMinZoomPreference(19);






  }
}

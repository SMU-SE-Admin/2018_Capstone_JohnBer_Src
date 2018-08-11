package smu.ac.kr.johnber.my;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.map.JBLocation;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.LocationUtil;
import smu.ac.kr.johnber.util.RecordUtil;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class MyCourseDetailFragment extends Fragment  implements OnMapReadyCallback {
    private final static String TAG = makeLogTag(MyCourseDetailFragment.class);
    public TextView courseName;
    public TextView startPoint;
    public TextView endPoint;
    public TextView distance;
    public TextView calories;
    public TextView time;
    private View mView;
    private Marker mMarker;
    private MapView mMapView;
    private GoogleMap mgoogleMap;
    private List<Record> data;
    private Record record;
    public MyCourseDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_my_course_detail, container, false);
        courseName = mView.findViewById(R.id.tv_my_course_detail_summary_title);
        startPoint = mView.findViewById(R.id.tv_my_course_detail_summary_start_point);
        endPoint = mView.findViewById(R.id.tv_my_course_detail_summary_end_point);
        distance = mView.findViewById(R.id.tv_my_course_distance);
        calories = mView.findViewById(R.id.tv_my_course_calories);
        time = mView.findViewById(R.id.tv_my_course_time);
        mMapView = this.mView.findViewById(R.id.mycourse_map_view);

        data = ((MyActivity)getActivity()).getRecordsItems();
        record = data.get(getArguments().getInt("position"));
        courseName.setText(record.getTitle());

        startPoint.setText(LocationUtil.latlngtoStringLocation(record.getJBLocation().get(0),getActivity().getApplicationContext()));
        endPoint.setText(LocationUtil.latlngtoStringLocation(record.getJBLocation().get(record.getJBLocation().size() - 1),getActivity().getApplicationContext()));
        String dist = RecordUtil.distanceToStringFormat(record.getDistance());
        distance.setText(dist);
        calories.setText(Double.toString(record.getCalories()));
        String stime = RecordUtil.milliseconsToStringFormat(record.getElapsedTime());
        time.setText(stime);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        return mView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onPause() {
        //fragment를 떠났을 때 유지시킬 데이터 저장
        super.onPause();
        mMapView.onPause();
        LOGD(TAG, "onPause");
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
//        googleMap.setMinZoomPreference(8);
        ArrayList<JBLocation> route = record.getJBLocation();
        mgoogleMap.addPolyline(setPolylineOptions(route));

        //마커 설정
        LatLng start = LocationUtil.jbLocationToLatLng(route.get(0));
        LatLng end = LocationUtil.jbLocationToLatLng(route.get(route.size() - 1));
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(start)
                .title("start")
                .icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, getActivity()));
        mgoogleMap.addMarker(startMarker).showInfoWindow();


        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(end)
                .title("end")
                .icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, getActivity()));
        mgoogleMap.addMarker(endMarker).showInfoWindow();
    //
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(JBLocation jbPoint : route) {
            LatLng point = LocationUtil.jbLocationToLatLng(jbPoint);
            builder.include(point);
        }
        //bound로 애니메이션
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = 300;
        int padding = 50;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding);
        mgoogleMap.moveCamera(cu);
    }

    public PolylineOptions setPolylineOptions(ArrayList<JBLocation> list) {
        PolylineOptions options = new PolylineOptions();
        for (JBLocation location : list) {
            options.add(LocationUtil.jbLocationToLatLng(location));
            LOGD(TAG, "[location] " + LocationUtil.jbLocationToLatLng(location));
        }
        options.width(15).color(Color.parseColor("#1D8BF8")).geodesic(true);
        LOGD(TAG, "sizeof array" + list.size());
        return options;
    }



    public String latlngtoStringLocation(JBLocation location) {
        String stringLocation=null;
        Geocoder mGeoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.KOREA);
        List <Address> address;

        try {
            address = mGeoCoder.getFromLocation(location.getmLatitude(), location.getmLongitude(), 1);
            if(address != null &&address.size()>0){
                stringLocation = address.get(0).getAddressLine(0).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringLocation;
    }

//    public String latlngtoStringLocation(JBLocation location) {
//        String stringLocation=null;
//        Geocoder mGeoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.KOREA);
//        List <Address> address;
//
//        try {
//            address = mGeoCoder.getFromLocation(location.getmLatitude(), location.getmLongitude(), 1);
//            if(address != null &&address.size()>0){
//                stringLocation = address.get(0).getAddressLine(0).toString();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return stringLocation;
//    }

}

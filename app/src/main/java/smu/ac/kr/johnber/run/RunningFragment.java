package smu.ac.kr.johnber.run;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.RecordUtil;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * - MAP view 설정 , 콜백 메소드
 * - 버튼 listener, action
 * - 위치 트래킹
 * - 경로좌표 저장
 * - 지도에 라인으로 표시
 * - 기록 측정
 * - UI View 업데이트
 */
public class RunningFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private static final float POLYLINE_STROKE_WIDTH_PX = 10;
    private static final int COLOR_BLACK_ARGB = R.color.colorAccent;
    private final static String TAG = makeLogTag(RunningFragment.class);
    private Activity mActivity;
    private TextView mDistance;
    private TextView mTime;
    private TextView mCalories;
    private Button mPauseButton;
    private View mView;
    private Button mRun;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNav;
    private View mWeatherWidgetView;
    private MapView mMapView;
    private GoogleMap mgoogleMap;
    private Marker mMarker;                     // 현재 위치를 표시할 마커
    private ArrayList<LatLng> locationArrayList = new ArrayList<>(); //마커, 폴리라인 표시를 위한 좌표 목록
    private ArrayList<LatLng> courseDetailLatLng;
    private String[] courseNames;
    private boolean isBound;
    private boolean isFromCourseRec;
    // service 객체를 onServiceConnected 에서 받아옴
    private TrackerService mTrackerService;
    private static int mState;
    private static final int INIT = 20000;
    private static final int START = 20001;
    private static final int PAUSE = 20002;
    private static final int RESUME = 20003;
    private static final int STOP = 20003;

    public RunningFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.run_running_fragment, container, false);

        if (savedInstanceState == null) {
            initView();
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        return mView;
    }

    @Override
    public void onAttach(Context context) {

        //Activity에 할당되었을 때 호출
        super.onAttach(context);
        mActivity = getActivity();
//        LOGD(TAG, "onAttached");

        //RUN버튼을 눌러 넘어온 경우이므로 INIT으로 세팅
        setState(INIT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        LOGD(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LOGD(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        bindTrackerService();
        LOGD(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LOGD(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD(TAG, "onDestroy");
        unbindTrackerService();
    }

    @Override
    public void onClick(View view) {

        //Pause버튼 눌렸을 때
        if (mTrackerService != null) {
            mTrackerService.stop();
        }

        //PauseFragment의 km, time, cal View에 세팅할 값 넘겨주기
        Bundle bundle = new Bundle();
        PauseRunningFragment pauseRunningFragment = new PauseRunningFragment();
        bundle.putString("time", txtTime);
        bundle.putString("distance", txtDistance);
        bundle.putString("calories", txtCalories);

        pauseRunningFragment.setArguments(bundle);

        //달리기 중지 fragment로 전환
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.run_running_status_container, pauseRunningFragment, "PAUSEFRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LOGD(TAG, "onMapReady()");
        mgoogleMap = googleMap;
        googleMap.setMinZoomPreference(19);
        Intent intent = getActivity().getIntent();
        LatLng centerPoint ;
        // Main화면에서 넘긴 좌표 꺼내기
        Double latitude = intent.getExtras().getDouble("latitude");
        Double longitude = intent.getExtras().getDouble("longitude");

        isFromCourseRec = (intent.getBooleanExtra("fromCourse", false)) ? true : false;

        if (isFromCourseRec) {
            courseDetailLatLng = new ArrayList();
            courseDetailLatLng.add(new LatLng(intent.getExtras().getDouble("course_slat")
                    , intent.getExtras().getDouble("course_slng")));
            courseDetailLatLng.add(new LatLng(intent.getExtras().getDouble("course_elat")
                    , intent.getExtras().getDouble("course_elng")));

            courseNames = new String[2];
            courseNames[0] = intent.getStringExtra("course_sName");
            courseNames[1] = intent.getStringExtra("course_eName");
            LOGD(TAG, courseNames[0] + courseNames[1]);

//FIXME  :
            double dist_S = RecordUtil.distance(latitude, longitude, courseDetailLatLng.get(0).latitude
                    , courseDetailLatLng.get(0).longitude);
            double dist_E = RecordUtil.distance(latitude,longitude,courseDetailLatLng.get(1).latitude
                    , courseDetailLatLng.get(1).longitude);
            double dist;
            double height;
            if ((dist_S < dist_E)) {
                dist = dist_S;
                height = SphericalUtil.computeHeading(new LatLng(latitude, longitude), courseDetailLatLng.get(0));

            } else {
                dist = dist_E;
                height = SphericalUtil.computeHeading(new LatLng(latitude, longitude), courseDetailLatLng.get(1));
            }

            centerPoint = SphericalUtil.computeOffset(new LatLng(latitude, longitude), dist * 0.5, height);

        } else {
            centerPoint =new LatLng(latitude, longitude);
        }

        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(centerPoint));

    }


    public void initView() {
        mMapView = mView.findViewById(R.id.running_mapview);
        mDistance = mView.findViewById(R.id.tv_run_distance);
        mTime = mView.findViewById(R.id.tv_run_time);
        mCalories = mView.findViewById(R.id.tv_run_calories);
        mPauseButton = mView.findViewById(R.id.btn_pause);
        mPauseButton.setOnClickListener(this);
    }

    /**
     * bind Service 객체 연결
     */
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //bind되었을 때 Service객체 가져오기
            mTrackerService = ((TrackerService.TrackerBinder) iBinder).getService();

            //callback 등록
            mTrackerService.registerCallback(mCallback);
//            Toast.makeText(mTrackerService, "TrackerService connected", Toast.LENGTH_SHORT).show();

            //기록 측정 시작
            if (mTrackerService != null) {
                mTrackerService.start(mState, mHandler);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            // 예기치 못한 상황으로 연결 실패
            LOGD(TAG, "TrackerService disConnected");

            //callback 해제
            mTrackerService.unregisterCallback(mCallback);
//            Toast.makeText(mTrackerService, "TrackerService disconnected", Toast.LENGTH_SHORT).show();
        }
    };


    //메세지 핸들러를 통한 ui변경
    TrackerService.TrackerCallback mCallback = new TrackerService.TrackerCallback() {
        @Override
        public void onDistanceChanged(double value) {
            Message msg = mHandler.obtainMessage(MSG_DISTANCE, (int) value, 0);
            LOGD(TAG, "distance: " + Integer.toString((int) value));
            mHandler.sendMessage(msg);
        }

        @Override
        public void onCaloriesChanged(double value) {
            Message msg = mHandler.obtainMessage(MSG_CALORIES);
            Bundle bundle = new Bundle();
            bundle.putString("calories", Integer.toString((int) value));
            msg.setData(bundle);
            mHandler.sendMessageDelayed(msg, 1000);
        }

        @Override
        public void onElapsedtimeChanged(double value) {

        }

        @Override
        public void onLocationChanged(double latitude, double longitude) {
            Message msg = mHandler.obtainMessage(MSG_LOCATION);
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

    };

    private static final int MSG_DISTANCE = 322;
    private static final int MSG_TIME = 323;
    private static final int MSG_CALORIES = 324;
    private static final int MSG_LOCATION = 325;
    private String txtTime;
    private String txtDistance;
    private String txtCalories;

    // Service에서 보낸 msg 수신을 위함
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        //메세지 수신
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISTANCE:
                    double distance = RecordUtil.metersToKillometers(msg.arg1);
                    txtDistance = RecordUtil.distanceToStringFormat(distance);
                    mDistance.setText(txtDistance);
                    break;
                case MSG_TIME:
                    Bundle bundle = msg.getData();
                    txtTime = bundle.getString("time");
                    mTime.setText(txtTime);
                    break;
                case MSG_CALORIES:
                    bundle = msg.getData();
                    txtCalories = bundle.getString("calories");

                    mCalories.setText(txtCalories);
                    break;
                case MSG_LOCATION:
                    bundle = msg.getData();
                    double latitude = bundle.getDouble("latitude");
                    double longitude = bundle.getDouble("longitude");
                    locationArrayList.add(new LatLng(latitude, longitude));

                    drawPolylines(locationArrayList);
                    setMarkers();

                    break;
                default:
                    super.handleMessage(msg);

            }

        }
    };

    // 지도에 이동 경로 표시
    private void drawPolylines(ArrayList<LatLng> locationArrayList) {
        PolylineOptions options = new PolylineOptions();
        options.addAll(locationArrayList).endCap(new RoundCap())
                .width(POLYLINE_STROKE_WIDTH_PX)
                .color(Color.parseColor("#1D8BF8")).geodesic(true)
                .jointType(JointType.ROUND)
                .geodesic(true);
        mgoogleMap.addPolyline(options);

    }

    // 지도에 마커 표시
    // 시작점, 끝점(현재위치)
    private void setMarkers() {
        LOGD(TAG, "setMarkers()");
        ArrayList<LatLng> markerList = new ArrayList<>();

        //시작 위치
        markerList.add(0, locationArrayList.get(0));

        //TODO : 코스에서 RUN하는경우 코스 시작, 종료지점 마커 추가

        if (locationArrayList.size() > 1) {

            // 끝점(현재위치)
            markerList.add(1, locationArrayList.get(locationArrayList.size() - 1));
        }

        //시작 지점
        MarkerOptions options1 = new MarkerOptions();
        options1.position(markerList.get(0)).title("출발");
        options1.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_flag, mActivity));
        mgoogleMap.addMarker(options1);

        //코스 마커
        // TODO : Puase 화면에서 코스 마커도 표시 ..?

        LOGD(TAG, "set markers : " + isFromCourseRec);
        if (isFromCourseRec) {
            for (LatLng point : courseDetailLatLng) {
                int index = 0;
                MarkerOptions options = new MarkerOptions();
                options.position(point).title(courseNames[index]);
                options.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, mActivity));
                mgoogleMap.addMarker(options).showInfoWindow();
                index++;
            }
        }

        //현재 지점
        if (markerList.size() > 1) {
            //이전 마커 삭제
            if (mMarker != null) {
                mMarker.remove();
            }
            MarkerOptions options2 = new MarkerOptions();
            options2.position(markerList.get(1)).title("도착");
            options2.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_flag, mActivity));
            mMarker = mgoogleMap.addMarker(options2);
        }


//        //카메라 이동
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for(LatLng point : locationArrayList) {
//            builder.include(point);
//        }
//        //bound로 애니메이션
//        LatLngBounds bounds = builder.build();
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = 300;
//        int padding = 50;
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding);
//        mgoogleMap.moveCamera(cu);
        //카메라 이동
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng
                (locationArrayList.get(locationArrayList.size() - 1)));
    }

    //bindService
    private void bindTrackerService() {
        LOGD(TAG, "TrackerService Connected");
        getActivity().startService(new Intent(getActivity(), TrackerService.class));
        //flag BIND_AUTO_CREATE 로 설정시 stopSelf()를 호출하여도 unbound 될 때까지 연결이 살아있기때문에 0으로 교체
        getActivity().bindService(new Intent(getActivity(), TrackerService.class), mConnection, 0);
        isBound = true;
    }

    //unbindService
    private void unbindTrackerService() {
        if (isBound) {
            LOGD(TAG, "TrackerService disconnected");
            getActivity().stopService(new Intent(getActivity(), TrackerService.class));
            getActivity().unbindService(mConnection);
        }
        isBound = false;

    }

    public void resumebindService() {
        LOGD(TAG, "resumebindService");
        setState(RESUME);

        bindTrackerService();
    }

    public void setState(int state) {
        switch (state) {
            case INIT:
                this.mState = INIT;
                break;
            case RESUME:
                this.mState = RESUME;
                break;

        }
    }

    public GoogleMap getGooglemap() {
        return mgoogleMap;
    }
}
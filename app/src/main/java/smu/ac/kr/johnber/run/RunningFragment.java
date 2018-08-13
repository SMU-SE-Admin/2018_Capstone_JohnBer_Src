package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Map;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.RecordUtil;

/**
 * - MAP view 설정 , 콜백 메소드
 * - 버튼 listener, actionㅓㄹ정
 * - 위치 트래킹
 * - 경로좌표 저장
 * - 지도에 라인으로 표시
 * - 기록 측정
 * - UI View 업데이트
 */
public class RunningFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

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
    private boolean isBound;
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
            /**
             * view 초기화 및 MapView 추가
             */
            //TODO : initview 오버라이딩
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
        LOGD(TAG, "onAttached");
        //RUN버튼을 눌러 넘어온 경우이므로 INIT으로 세팅
        setState(INIT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LOGD(TAG, "onActivityCreated");
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
        bundle.putString("calories",txtCalories);

        //TODO : 오늘 날짜 넘겨주기 + 포맷형식은 Util에 구현?~  tracker Service에서 sharedPreferences에 저장한것을 PauseRunningFrragment에서 불러와서 세팅
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
        //TODO : 기본위치 - 현재위치로
        // GPS : 확인
        mgoogleMap = googleMap;
        googleMap.setMinZoomPreference(19);

        // Main화면에서 넘긴 좌표 꺼내기
        Intent intent = getActivity().getIntent();
        Double latitude = intent.getExtras().getDouble("latitude");
        Double longitude = intent.getExtras().getDouble("longitude");
        LatLng defaultLatLng = new LatLng(latitude,longitude);
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
    }

//  private void hideActivityContainer() {
//    mRun = getActivity().findViewById(R.id.btn_run);
//    mToolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
//    mBottomNav = getActivity().findViewById(R.id.bottomNavigationView);
//    mWeatherWidgetView = getActivity().findViewById(R.id.v_weatherWidget_container);
//
//    mRun.setVisibility(View.GONE);
//    mToolbar.setVisibility(View.GONE);
//    mBottomNav.setVisibility(View.GONE);
//    mWeatherWidgetView.setVisibility(View.GONE);
//  }
//
//  private void showActivityContainer() {
//    mRun.setVisibility(View.VISIBLE);
//    mToolbar.setVisibility(View.VISIBLE);
//    mBottomNav.setVisibility(View.VISIBLE);
//    mWeatherWidgetView.setVisibility(View.GONE);
//  }


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
            Toast.makeText(mTrackerService, "TrackerService connected", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mTrackerService, "TrackerService disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    TrackerService.TrackerCallback mCallback = new TrackerService.TrackerCallback() {
        //메세지 핸들러를 통한 ui변경
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
            //TODO: 현재 위치 표시 마커 추가
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
        options.addAll(locationArrayList).width(25).color(Color.parseColor("#1D8BF8")).geodesic(true);
        mgoogleMap.addPolyline(options);

    }
    // 지도에 마커 표시
    // 시작점, 끝점(현재위치)
    private void setMarkers() {
        ArrayList<LatLng> markerList = new ArrayList<>();
        markerList.add(0,locationArrayList.get(0));
        if (locationArrayList.size() > 1) {

        markerList.add(1,locationArrayList.get(locationArrayList.size() - 1)); // 끝점(현재위치)
        }
        //시작 지점
        MarkerOptions options1 = new MarkerOptions();
        options1.position(markerList.get(0)).title("start");
        options1.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_flag,mActivity));
        mgoogleMap.addMarker(options1);

        //현재 지점
        if (markerList.size() > 1) {
            //이전 마커 삭제
            if (mMarker != null) {
                mMarker.remove();
            }
            MarkerOptions options2 = new MarkerOptions();
            options2.position(markerList.get(1)).title("end");
            options2.icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current,mActivity));
            mMarker = mgoogleMap.addMarker(options2);
        }

        // 카메라 이동
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng
                (locationArrayList.get(locationArrayList.size()-1)));
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

    public GoogleMap getGooglemap(){
        return mgoogleMap;
    }
}


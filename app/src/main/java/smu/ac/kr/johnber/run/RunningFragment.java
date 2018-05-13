package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import smu.ac.kr.johnber.R;
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
  private Marker mMarkerFrom;
  private Marker mMarkerTo;

  private boolean isRunning;
  private boolean isBound;
  // service 객체를 onServiceConnected 에서 받아옴
  private TrackerService mTrackerService;
  private Timer mTimer;
  private static final int INIT = 20000;
  private static final int START = 20001;
  private static final int PAUSE = 20002;
  private static final int RESUME = 20003;
  private static final int STOP = 20003;
  private static int state;
  public RunningFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);



  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.run_running_fragment, container, false);
    if(savedInstanceState == null) {
      /**
       * view 초기화 및 MapView 추가
       */
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
    LOGD(TAG,"onAttached");
    //TODO:  MAP
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LOGD(TAG, "onActivityCreated");

    //Fragment 화면 full screen으로 만듬
//    hideActivityContainer();
  }

  @Override
  public void onStart() {
    super.onStart();
    LOGD(TAG,"onStart");

  }

  @Override
  public void onPause() {
    super.onPause();
    LOGD(TAG,"onPause");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

  }

  @Override
  public void onResume() {
    super.onResume();
    bindTrackerService();
    LOGD(TAG,"onResume");
  }

  public void initView() {
    mMapView = mView.findViewById(R.id.running_mapview);
    mDistance = mView.findViewById(R.id.tv_run_distance);
    mTime = mView.findViewById(R.id.tv_run_time);
    mCalories = mView.findViewById(R.id.tv_run_calories);
    mPauseButton = mView.findViewById(R.id.btn_pause);
    mPauseButton.setOnClickListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOGD(TAG, "onDestroy");
    unbindTrackerService();
//    showActivityContainer();
  }

  @Override
  public void onClick(View view) {
    //Pause
    if (mTrackerService != null) {
      mTrackerService.stop();
      mTimer.stop();
    }
    //달리기 중지 fragment
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.run_running_status_container, new PauseRunningFragment(), "PAUSEFRAGMENT")
            .addToBackStack(null)
            .commit();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mgoogleMap=googleMap;
    googleMap.setMinZoomPreference(17);
    LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,18));
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



  /**
   * bind Service 객체 연결
   */
   ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      //bind되었을 때 Service객체 가져오기
      mTrackerService = ((TrackerService.TrackerBinder)iBinder).getService();
      Toast.makeText(mTrackerService, "TrackerService connected", Toast.LENGTH_SHORT).show();
      //callback 등록
      mTrackerService.registerCallback(mCallback);
      //타이머
      mTimer = new Timer();
      //기록 측정 시작
      if (mTrackerService != null) {
        mTrackerService.start(state);
        mTimer.start(mHandler,INIT);


      }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      // 예기치 못한 상황으로 연결 실패
      LOGD(TAG,"TrackerService disConnected");
      Toast.makeText(mTrackerService, "TrackerService disconnected", Toast.LENGTH_SHORT).show();
      //callback 해제
      mTrackerService.unregisterCallback(mCallback);
    }
  };

  TrackerService.TrackerCallback mCallback = new TrackerService.TrackerCallback() {
    //메세지 핸들러를 통한 ui변경
    @Override
    public void onDistanceChanged(double value) {
      Message msg = mHandler.obtainMessage(MSG_DISTANCE, (int)value, 0);
      LOGD(TAG,"distance: "+Integer.toString((int)value));
      mHandler.sendMessage(msg);


    }

    @Override
    public void onCaloriesChanged(double value) {
        Message msg = mHandler.obtainMessage(MSG_CALORIES);
        Bundle bundle = new Bundle();
        bundle.putString("calories", Integer.toString((int)value));
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg,1000);
    }

    @Override
    public void onElapsedtimeChanged(double value) {

    }
      /**
       * @param from last location
       * @param to current location
       */
    @Override
    public void onLocationChanged(LatLng from, LatLng to) {
      //TODO:폴리라인그리기   https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/DistanceDemoActivity.java
      //Bundle에 데이터를 담아 message.setData(bundle)후 보내 http://al02000.tistory.com/9
      //TODO: 현재 위치 표시 마커 추가
      Message msg = mHandler.obtainMessage(MSG_LOCATION);
      Bundle bundle = new Bundle();
//      bundle.put
    }

    @Override
    public void onPausedLisenter(Record record) {
      //call activity's setrecord
      Message msg = mHandler.obtainMessage(PAUSE,record);
      mHandler.sendMessage(msg);
    }
  };



  private static final int MSG_DISTANCE =322;
  private static final int MSG_TIME = 323;
  private static final int MSG_CALORIES =324;
  private static final int MSG_LOCATION =325;
// Service에서 보낸 msg 수신을 위함
  private Handler mHandler = new Handler(){
    //메세지 수신
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_DISTANCE:
          double distance = RecordUtil.metersToKillometers(msg.arg1);
          String distanceString  = String.format("%.2f", distance);
          mDistance.setText(distanceString);
          break;
        case MSG_TIME:
          Bundle bundle = msg.getData();
          String time = bundle.getString("time");
          mTime.setText(time);
          break;
        case MSG_CALORIES:
           bundle = msg.getData();
          String calories = bundle.getString("calories");
          mCalories.setText(calories);
          break;
        default:
           super.handleMessage(msg);

      }

    }
  };
   //TODO : bindService
  private void bindTrackerService(){
    LOGD(TAG,"TrackerService Connected");
    //TODO : intent action으로 stop인지... 상태
    getActivity().startService(new Intent(getActivity(), TrackerService.class));
    //flag BIND_AUTO_CREATE 로 설정시 stopSelf()를 호출하여도 unbound 될 때까지 연결이 살아있기때문에 0으로 교체
    getActivity().bindService(new Intent(getActivity(), TrackerService.class), mConnection, 0);
    isBound = true;
  }

  //TODO : unbindService
  private void unbindTrackerService(){
    if (isBound) {
      LOGD(TAG,"TrackerService disconnected");
      getActivity().stopService(new Intent(getActivity(), TrackerService.class));
      getActivity().unbindService(mConnection);
    }
    isBound = false;

  }

  public void setState(int state){
    switch (state) {
      case INIT:
        this.state =INIT;
        break;
      case RESUME :
        this.state = RESUME;
        break;

    }
  }

  public void resumebindService(){
    LOGD(TAG, "resumebindService");
    bindTrackerService();
  }

}


package smu.ac.kr.johnber.run;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Date;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * RunningFragment 와 통신
 *  - data의 전송은 callback listener을 사
 * 달리기 기록 측정을 위한 Service
 */
public class TrackerService extends Service {

    private final String TAG = "TrackerService.class";
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    // status flag
    private static final int INIT = 20000;
    private static final int START = 20001;
    private static final int PAUSE = 20002;
    private static final int RESUME = 20003;
    private static final int STOP = 20003;

    private final IBinder mIBinder = new TrackerBinder();
    private TrackerCallback mtrackerCallback;

    private int mState;
    private double distance;
    private double elapsedTime;
    private double calories;
    private ArrayList<LatLng> location;
    private Location mCurrentLocation;
    private Location mLastLocation;
    private Location mActivityLastLocation; // 처음 start할때 location
    private Date date;
    private int startTime;
    private int currentTime;
    private String title;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
//    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;


    public TrackerService() {
    }

    /**
     * 서비스에서 제공하는 public 함수들을 사용하기 위한 통신채널
     *
     */
    public class TrackerBinder extends Binder {
        //외부에서 함수 호출시 RecService의 레퍼런스를 돌려줄 수 있도록함
        public TrackerService getService(){
            return TrackerService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LOGD(TAG,"onCreate");
        // location 받아오기위한 준비
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = getLocationRequest();
        createLocationCallback();
        //변수 초기화
        if(mState != RESUME){
            initService();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOGD(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LOGD(TAG,"onBind");
        // TODO: Return the communication channel to the service.

       return mIBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD(TAG, "onDestroy");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * 운동기록 변화에 따라 RunningActivity UI반영을 위한 콜백 메소드
     */
    public interface TrackerCallback {
        public void onDistanceChanged(double value);
        public void onCaloriesChanged(double value);
        public void onElapsedtimeChanged(double value);
        public void onLocationChanged(LatLng value);
    }

//    @SuppressLint("MissingPermission")
//    private void startLocationTracking() {
//        LOGD(TAG, "enableLocationTracking : check permission");
//                //TODO : request current location
//                LOGD(TAG, "Start location tracking");
//                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//    }
    /**
     * LocationRequest 설정
     - interval
     - fastest interval
     - priority
     */
    private LocationRequest getLocationRequest() {
        @SuppressLint("RestrictedApi")
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        //5 seconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     mFusedLocationClient로 부터 위치를 받았을 때 실행할 콜백 메소드
     매 위치가 바뀔때마다 기록 측정
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // INIT -> START하는경우 LastLocation 설정
                if (mState == INIT) {
                    //RUN버튼 누른 시점의 위치
                    mActivityLastLocation = locationResult.getLastLocation();
                    mLastLocation = mActivityLastLocation;
                }
                startImpl(locationResult);
            }
        };
    }

    // 변수들 초기화
    public void initService(){
        mState = INIT;
        distance = 0;
        elapsedTime = 0;
        calories = 0;
         location = null;
         mCurrentLocation = null;
        mLastLocation = null;
        mActivityLastLocation = null;
         date = null;
        startTime = 0;
        currentTime = 0;
    }
    /**
     * state 확인 ( resume인경우 이전값에 이어서 측정)
     *  locationrequestUpdates (onLocationResult콜백이 호출되고, 여기서 운동기록 측정 함수 호출)
     *  Timer.start
     */
    @SuppressLint("MissingPermission")
    public void start() {
        LOGD(TAG, "start tracking");
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        //기록 계산은 requsetLocationUpdates()에 따른 콜백 메소드인 onLocationResult에서 이루어짐 -> startImpl()
    }
    // TODO : 운동기록 측정 함수  , mCallback의 각 함수 호출
    public void startImpl(LocationResult locationResult){
        mState = START;


        //거리 측정
        mCurrentLocation = locationResult.getLastLocation();
        LatLng from = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng to = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        distance += SphericalUtil.computeDistanceBetween(from,to);
        LOGD(TAG, "기록 측정"+"Current: "+from.toString()+"  Last: "+to.toString()+" distance: "+Integer.toString((int)distance));
        //runningfragment에서 오버라이딩한 onDistanceChanged call
        //TODO : distance에 더해주기
        mtrackerCallback.onDistanceChanged(distance);


    }
    /**
     * 기록 임시저장 -> sharedPreference에..
     * removeFusedLocation
     * Timer.pause
     * stopSelf()
     */
    public void pause() {

    }

    /**
     * start
     */
    public void resume() {

    }

    /**
     * save 기록
     * 자원 해제
     * stopSelf()
     */
    public void stop() {

    }

    /**
     * RunningFragment에 데이터 전달을 위한 callback listener를 등록/해제
     */
    public void registerCallback(TrackerCallback callback){
        mtrackerCallback = callback;
    }

    public void unregisterCallback(TrackerCallback callback){
        mtrackerCallback = null;
    }

}

package smu.ac.kr.johnber.run;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import smu.ac.kr.johnber.util.RecordUtil;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * RunningFragment 와 통신
 *  - data의 전송은 callback listener을 사용
 * 달리기 기록 측정을 위한 Service
 * requestLocationUpdates()할 때 마다 거리, 칼로리를 계산한다.
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
    private Record mRecord;
    private int mState;
    private double distance;
    private double elapsedTime;
    private double calories;
//    private ArrayList<LatLng> location;
    private Location mCurrentLocation;
    private Location mLastLocation;
    private Location mActivityLastLocation; // 처음 start할때 location
    private ArrayList<Location> locationArrayList = new ArrayList<Location>();
    private Date date;
    private double startTime;
    private double currentTime;
    private String title;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
//    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
//TODO : user 객체를 앱 로그인 성공후 사용자 만들어 놓고 weight만 getter로 받아와서 사용할 수 있도록 하기
    private double weight = 70.0;

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
        public void onLocationChanged(LatLng from, LatLng value);
//TODO:
        public void onPausedLisenter(Record record);
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
                    //ArrayList에 시작 location 저장
                    mLastLocation = mActivityLastLocation;

                    locationArrayList.add(mLastLocation);

                }
                startImpl(locationResult);
            }
        };
    }

    // 변수들 초기화
    public void init(){
        mState = INIT;
        distance = 0;
        elapsedTime = 0;
        calories = 0;
//        location = null;
        mCurrentLocation = null;
        mLastLocation = null;
        mActivityLastLocation = null;
        if (locationArrayList != null) {
            if (!locationArrayList.isEmpty()) {
                locationArrayList.clear();
                LOGD(TAG,"locationList cleared"+locationArrayList.size());
            }
        }
        currentTime = 0;
        startTime = SystemClock.elapsedRealtime();
        date = new Date(System.currentTimeMillis());
    }
    /**
     * state 확인 ( resume인경우 이전값에 이어서 측정)
     *  locationrequestUpdates (onLocationResult콜백이 호출되고, 여기서 운동기록 측정 함수 호출)
     *  Timer.start
     */
    @SuppressLint("MissingPermission")
    public void start(int Action) {

        LOGD(TAG, "start tracking");
        // set state
        switch (Action) {
            case INIT:
                mState = INIT;
                break;
            case RESUME:
                mState = RESUME;
                break;
        }

        if(mState != RESUME){
            //init variables
            init();
        } else if (mState == RESUME) {
            //reload variables from sharedPreferences
            resume();
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        //기록 계산은 requsetLocationUpdates()에 따른 콜백 메소드인 onLocationResult에서 이루어짐 -> startImpl()
        //변수 초기화

    }

    // TODO : 운동기록 측정 함수  , mCallback의 각 함수 호출
    public void startImpl(LocationResult locationResult){
        mState = START;

        //거리 측정
        mCurrentLocation = locationResult.getLastLocation();
        //ArrayList에 location 저장
        locationArrayList.add(mCurrentLocation);
        LOGD(TAG,"size of location list"+locationArrayList.size());
        LatLng from = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng to = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if(!from.equals(to))
            //mLastLocation~mCurrentLocation간 거리 구하기
          distance +=  SphericalUtil.computeDistanceBetween(from,to);
        LOGD(TAG, "기록 측정"+"Current: "+from.toString()+"  Last: "+to.toString()+" distance: "+Integer.toString((int)distance));
        //runningfragment에서 오버라이딩한 onDistanceChanged를 호출하여 UI업데이트
        mtrackerCallback.onDistanceChanged(distance);
        mtrackerCallback.onLocationChanged(from,to);
        //운동시간
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        //평균속도
        double averageSpeed = distance / elapsedTime;
        LOGD(TAG, "sppeed: " + averageSpeed+" dist and elapsed "+distance+"  "+elapsedTime);
        //칼로리 계산
        calories = RecordUtil.getAverageCalories(weight, elapsedTime);
        LOGD(TAG,"calories:"+calories);
        mtrackerCallback.onCaloriesChanged(calories);
        //LastLocation 업데이트
        mLastLocation = mCurrentLocation;
    }


    /**
     * sharedPreference로 부터 데이터 복원
     * - distance
     * - calories
     * - elapsedTime
     * - startTime
     * - locationArrayList
     * - lastLocation
     *      locationArrayList.get(마지막)값으로 복원
     */
    public void resume() {
        SharedPreferences preferences;
        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = preferences.getString("LOCATIONLIST", "");
        if(locationArrayList != null) {
        locationArrayList = gson.fromJson(response, new TypeToken<List<Location>>(){}.getType());
            LOGD(TAG, "size of saved array list " + locationArrayList.size());
            mLastLocation = locationArrayList.get(locationArrayList.size() - 1);
            LOGD(TAG, "lat of last location" + mLastLocation.getLatitude());
        }
        // 나머지 복원
        distance = Double.parseDouble(preferences.getString("DISTANCE", ""));
        elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", ""));
        calories = Double.parseDouble(preferences.getString("CALORIES", ""));
        startTime = Double.parseDouble(preferences.getString("STARTTIME", ""));
        LOGD(TAG, "distance: " + distance + " elapsedTime: " + elapsedTime + " statTime: " + startTime);
        mState = RESUME;
    }

    /**
     * save 기록
     * 자원 해제
     * stopSelf()
     */
    public void stop() {
        double endTime = SystemClock.elapsedRealtime();
//        mRecord = new Record(distance,elapsedTime,calories, locationArrayList,date, startTime, endTime, date.toString());
        /**
         * sharedPreference에 Record값 저장
         */
        //TODO:
        //locaitonArrayList저장
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(locationArrayList);
        editor = preferences.edit();
        editor.remove("LOCATIONLIST").commit();
        editor.putString("LOCATIONLIST", json);

        // 나머지 저장
        //TODO : String으로 변환해 저장 한 후 데이터 복원시 double로 다시 복원
        editor.putString("DISTANCE", Double.toString(distance));
        editor.putString("ELAPSEDTIME", Double.toString(elapsedTime));
        editor.putString("CALORIES", Double.toString(calories));
        //TODO : Date와 Title은 firebase에 Record 객체를 저장하기 전에 설정할것
        editor.putString("STARTTIME", Double.toString(startTime));
        editor.putString("ENDTIME", Double.toString(endTime));
        editor.commit();

        //서비스 종료
        this.stopSelf();
        LOGD(TAG, "stopTrackerService");
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

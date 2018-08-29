package smu.ac.kr.johnber.run;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import smu.ac.kr.johnber.map.JBLocation;
import smu.ac.kr.johnber.util.LocationUtil;
import smu.ac.kr.johnber.util.RecordUtil;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * 달리기 기록 측정을 위한 Service
 * - RunningFragment에서 Service를 bind시킨다.
 * - data의 전송은 callback listener을 사용
 * - 위치가 바뀔 때 마다 운동기록을 업데이트한다.
 */
public class TrackerService extends Service {

    private final String TAG = "TrackerService.class";
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private int mState;
    private static final int INIT = 20000;
    private static final int START = 20001;
    private static final int PAUSE = 20002;
    private static final int RESUME = 20003;
    private static final int STOP = 20003;

    // RunningFragment에서 TrackerService의 함수를 사용하기 위함
    private final IBinder mIBinder = new TrackerBinder();
    private TrackerCallback mtrackerCallback;

    private Record mRecord;
    private double distance;
    private double elapsedTime;
    private double calories;
    private Location mCurrentLocation;
    private Location mLastLocation;
    private Location mActivityLastLocation; // 처음 start할때 location
    private ArrayList<JBLocation> locationArrayList = new ArrayList<JBLocation>();
    private Date date;
    private double startTime;
    private double currentTime;
    private String title;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    //    private JBLocation mCurrentLocation;
    private LocationCallback mLocationCallback;
    private Handler mHandler;
<<<<<<< HEAD
    //TODO : user 객체를 앱 로그인 성공후 사용자 만들어 놓고 weight만 getter로 받아와서 사용할 수 있도록 하기
    //private double weight = 70.0;
=======
    //TODO [ 경환 ] : shared preference에서 몸무게 꺼내와서 멤버변수에 저장할것
    private double weight = 70.0;
>>>>>>> 1cdfbe563869b247caaa6b8c46398a8554a14939
    private Timer mTimer;

    public TrackerService() {
    }

    /**
     * 서비스에서 제공하는 public 함수들을 사용하기 위한 통신채널
     */
    public class TrackerBinder extends Binder {
        //외부에서 함수 호출시 RecService의 레퍼런스를 돌려줄 수 있도록함
        public TrackerService getService() {
            return TrackerService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LOGD(TAG, "onCreate");
        // location 받아오기위한 준비
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = getLocationRequest();
        createLocationCallback();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startService()에 의해 호출
        LOGD(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LOGD(TAG, "onBind");
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
     * 운동기록 변화에 따라 RunningActivity UI반영을 위한 콜백 리스너
     * RunningActivity에서 오버라이드한 콜백메소드에서 UI스레드로 Message를 통해 값을 전달
     */
    public interface TrackerCallback {
        public void onDistanceChanged(double value);
        public void onCaloriesChanged(double value);
        public void onElapsedtimeChanged(double value);
        public void onLocationChanged(double latitude, double longitude);
    }

//    @SuppressLint("MissingPermission")
//    private void startLocationTracking() {
//        LOGD(TAG, "enableLocationTracking : check permission");
//                //TODO : request current location
//                LOGD(TAG, "Start location tracking");
//                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//    }

    /**
     * LocationRequest를 설정
     * - interval
     * - fastest interval
     * - priority
     */
    private LocationRequest getLocationRequest() {
        @SuppressLint("RestrictedApi")
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        //5 seconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * mFusedLocationClient로 부터 위치를 받았을 때 실행할 콜백 메소드
     * 5초간격으로 위치를 요청하며
     * {@link #startImpl(LocationResult)}에서 운동기록을 측정한다.
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
                    locationArrayList.add(LocationUtil.locationTojbLocation(mLastLocation));
                }
                startImpl(locationResult);
            }
        };
    }

    // 변수 초기화
    public void init() {
        //TODO: Course - RUN - 해당 코스 정보 받아와서  split [ realmUID(코스)/행정구역(getfromlocation) ]
        //TODO: JBLocation에 lat,lng,flag(시작또는 종료 코스지점거쳐갔는지 유무)필드 추가
        mState = INIT;
        distance = 0;
        elapsedTime = 0;
        calories = 0;
        mCurrentLocation = null;
        mLastLocation = null;
        mActivityLastLocation = null;
        if (locationArrayList != null) {
            if (!locationArrayList.isEmpty()) {
                locationArrayList.clear();
//                LOGD(TAG, "Init Service...list size is " + locationArrayList.size());
            }
        }
        currentTime = 0;
        startTime = SystemClock.elapsedRealtime();
        date = new Date(System.currentTimeMillis());
    }

    /** 달리기 시작/재개 상태를 파악하여 Service를 init 또는 resume 상태로 값을 초기화한다.
     * @param Action : RunningFragment에서 Serivce 시작시 INIT 또는 RESUME 상태를 넘겨준다.
     * {@link #startImpl(LocationResult)}호출에 따라 운동기록을 측정 시작
     * - state 확인 (resume인경우 이전값에 이어서 측정)
     * - locationrequestUpdates (onLocationResult콜백이 호출되고, 여기서 운동기록 측정 함수 호출)
     * - Timer쓰레드 start
     */
    @SuppressLint("MissingPermission")
    public void start(int Action,Handler handler) {
//        LOGD(TAG, "Start TrackerService...current state(01init 03-resume) : " + mState);
        mHandler = handler;
        mTimer = new Timer(this);
        // set state
        switch (Action) {
            case INIT:
                mState = INIT;
                break;
            case RESUME:
                mState = RESUME;
                break;
        }

        if (mState != RESUME) {
            //init variables
            init();
//            LOGD(TAG, "startTimer init " + mState);
            mTimer.start(mHandler, mState);
        } else if (mState == RESUME) {
            //reload variables from sharedPreferences
            resume();
//            LOGD(TAG, "resumet Timer init " + mState);
            mTimer.start(mHandler, mState);
        }
        //기록 계산은 requsetLocationUpdates()에 따른 콜백 메소드인 onLocationResult에서 이루어짐 -> startImpl()
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    /**
     * 실질적인 운동기록 측정이 이루어지는 함수
      */
    public void startImpl(LocationResult locationResult) {
        mState = START;
        //거리 측정
        mCurrentLocation = locationResult.getLastLocation();
        //ArrayList에 location 저장
        locationArrayList.add(LocationUtil.locationTojbLocation(mCurrentLocation));

        LatLng from = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng to = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if (!from.equals(to)){
            //mLastLocation ~ mCurrentLocation 거리 구하기
            distance += SphericalUtil.computeDistanceBetween(from, to);
            //TODO : 코스시작지점, 종료지점 거쳤는지 체크 (반경300m?) - true인경우 / isconquered flag true, jblocation 내의 flag도 true
        }
        //운동시간
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        //TODO [ 경환 ]:  평균속도
        double averageSpeed = distance / elapsedTime;

        //칼로리 계산 jgh
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        double weight = pref.getFloat("userWeight", 0);
        Log.d("mainactivity", "fuckda;fioasdlkfjas;lkfj;alsdfjal" +weight);

        //TODO [ 경환 ]: 칼로리 계산

        calories = RecordUtil.getAverageCalories(weight, elapsedTime);

        //runningfragment에서 콜백 리스너를 호출하여 UI 업데이트
        mtrackerCallback.onCaloriesChanged(calories);
        mtrackerCallback.onDistanceChanged(distance);
        mtrackerCallback.onLocationChanged(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        LOGD(TAG, "[Lap] " +"CurrentLoc: "+to+" LastLoc: "+from+" Dist: "+distance+" ElapsedTime: "+RecordUtil.milliseconsToStringFormat(elapsedTime)+" Cal: "+calories+" Av.Speed: "+averageSpeed+" startTime: "+startTime);
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
     * locationArrayList.get(마지막)값으로 복원
     */
    public void resume() {
        SharedPreferences preferences;
        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);
//        Gson gson = new Gson();
        String response = preferences.getString("LOCATIONLIST", "");
//        LOGD(TAG, "saved locations in JSON : " + response);

        if (locationArrayList != null) {
            locationArrayList.clear();
//            LOGD(TAG, "cleared locationList :  " + locationArrayList.size());
        }
////TODO: " Course - RUN - 해당 코스 정보 받아와서  split [ realmUID(코스)/행정구역(getfromlocation) ] " 값 복원
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        locationArrayList = gson.fromJson(response, new TypeToken<ArrayList<JBLocation>>() {
        }.getType());
        LOGD(TAG, "saved locationList size :  " + locationArrayList.size());
        JBLocation jbLastlocation =  locationArrayList.get(locationArrayList.size() - 1);
        mLastLocation = LocationUtil.jbLocationToLocation(jbLastlocation);
        LOGD(TAG, "saved LastLoc: " + mLastLocation+" get lat " + mLastLocation.getLatitude());

        // 나머지 복원
        distance = Double.parseDouble(preferences.getString("DISTANCE", ""));
        elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", ""));
        //TODO [경환]  : 칼로리 복원  - 값 제대로 가져왔는지만 확인하면됨.
        calories = Double.parseDouble(preferences.getString("CALORIES", ""));
        startTime = Double.parseDouble(preferences.getString("STARTTIME", ""));
//        LOGD(TAG, "[Restored Lap] " +"CurrentLoc: "+mCurrentLocation+" LastLoc: "+mLastLocation+" Dist: "+distance+" ElapsedTime: "+RecordUtil.milliseconsToStringFormat(elapsedTime)+" Cal: "+" startTime: "+startTime);
        mState = RESUME;
    }


    /**
     * save 기록
     * 자원 해제
     * stopSelf()
     * sharedPreference에 Record값 저장
     */
    public void stop() {
        double endTime = SystemClock.elapsedRealtime();
//        mRecord = new Record(distance,elapsedTime,calories, locationArrayList,date, startTime, endTime, date.toString());
        //locaitonArrayList저장
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        String json = gson.toJson(locationArrayList);
        editor = preferences.edit();
        editor.putString("LOCATIONLIST", json);
        editor.apply();
//TODO: " Course - RUN - 해당 코스 정보 받아와서  split [ realmUID(코스)/행정구역(getfromlocation) ] " 값 저장 + isCounquered? 플래그 ~ 시작코스, 종료코스 한번이라도 거쳐간경우
        // 나머지 저장
        // String으로 변환해 저장 한 후 데이터 복원시 double로 다시 복원
        editor.putString("DISTANCE", Double.toString(distance));
        editor.putString("ELAPSEDTIME", Double.toString(elapsedTime));
        editor.putString("CALORIES", Double.toString(calories));
        //TODO : Date와 Title설정할것
        editor.putString("STARTTIME", Double.toString(startTime));
        editor.putString("ENDTIME", Double.toString(endTime));
        String currentDateandTime = RecordUtil.getFormattedDate();
        editor.putString("DATE",currentDateandTime);
        editor.commit();

        Log.d("mainactivity", "TrackerService Stop Calories : "+ preferences.getString("CALORIES", ""));

        //서비스 종료
        this.stopSelf();
        mTimer.stop();
//        LOGD(TAG, "[Stopped Lap] " +"ElapsedTime: "+RecordUtil.milliseconsToStringFormat(elapsedTime)+" Cal: "+" startTime: "+startTime);
//        LOGD(TAG, "stopTrackerService");
    }

    /**
     * RunningFragment에 데이터 전달을 위한 callback listener를 등록/해제
     */
    public void registerCallback(TrackerCallback callback) {
        mtrackerCallback = callback;
    }

    public void unregisterCallback(TrackerCallback callback) {
        mtrackerCallback = null;
    }

}

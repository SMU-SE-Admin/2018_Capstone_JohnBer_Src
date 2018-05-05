package smu.ac.kr.johnber.run;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.WeatherForecast;
import smu.ac.kr.johnber.util.LogUtils;
import smu.ac.kr.johnber.util.PermissionUtil;

/**
 * 메인화면
 * - 위치, 네트워크. 퍼미션 세팅/ 체크
 * - 지도 설정
 * - 동네예보 주기적으로 싱크
 * - 현재 위치 표시
 * - 위도 경도값 알아내기
 */
//TODO: 지도 처리
public class MainActivity extends BaseActivity implements OnClickListener, OnMapReadyCallback {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private TextView mRegion;
    private TextView mSky;
    private TextView mCelcius;

    private Button mRun;

    private MapView mMapview;
    private GoogleMap mgoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_main);
        initView();
        seListeners();
        //TODO: 일정 시간마다 데이터를 갱신해야함

        /**
         * 지도 설정 & 위치 트래킹
         */
        mMapview.onCreate(savedInstanceState);
        //Initialize mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = getLocationRequest();
        createLocationCallback();
        //callback on onMapReady
        mMapview.getMapAsync(this);

        WeatherForecast.loadWeatherData(this);
        LOGD(TAG, "onCreate");


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapview.onStart();
        LOGD(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
        enableLocationTracking();
        LOGD(TAG, "onResume");


    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
        LOGD(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapview.onStop();
        LOGD(TAG, "onStop");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LOGD(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
        LOGD(TAG, "onDestroy");
        //location tracking 해제
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected int getNavigationItemID() {
        return R.id.action_run;
    }

    /**
     * view 초기화 설정
     */
    public void initView() {
        mRun = findViewById(R.id.btn_run);
        mMapview = findViewById(R.id.main_mapview);
    }

    public void seListeners() {
        mRun.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //달리기 실행전 권한 체크
        if(!PermissionUtil.shouldAskPermission(this,PERMISSION)){
            //권한 있음
//            mRun.setVisibility(view.GONE);
//            RunningFragment runningFragment = new RunningFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.homeContainer, runningFragment, "RUNNINGFRAGMENT")
//                    .addToBackStack(null)
//                    .commit();
            Intent intent = new Intent(this, RunningActivity.class);
            startActivity(intent);
        }else
            PermissionUtil.checkPermission(this,PERMISSION,REQUEST_LOCATION_PERMISSION);
    }

    // 퍼미션 요청에 대한 call back
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case (REQUEST_LOCATION_PERMISSION):
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허가
                    PermissionUtil.makePermissionRationaleSnackbar(this, "permission granted");
                } else {
                    //권한 거부
                    //안내 메세지 snack bar
                    if (PermissionUtil.shouldShowRationale(this, PERMISSION))
                        PermissionUtil.makePermissionRationaleSnackbar(this);
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        if (PermissionUtil.shouldAskPermission(this, PERMISSION)) {
            //권한없는경우 default 서울로 설정 + 안내 문구
            googleMap.setMinZoomPreference(17);
            LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
            mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,18));
            PermissionUtil.makePermissionRationaleSnackbar(this);
            return;
        }
        //googleMap 준비가 끝나면 location tracking
        enableLocationTracking();
        //위치 권한있음
    }

    /**
     * Start location tracking
     */
    @SuppressLint("MissingPermission")
    private void enableLocationTracking() {
        LOGD(TAG, "enableLocationTracking : check permission");
        if (!PermissionUtil.shouldAskPermission(this, PERMISSION)) {
            //권한 있음
            if (mgoogleMap != null) {
                mgoogleMap.setMyLocationEnabled(true);
                //TODO : request current location
                LOGD(TAG, "Start location tracking");
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                return;
            }
        }

        LOGD(TAG, "enableLocationTracking : have no permission");
        PermissionUtil.checkPermission(this, PERMISSION, REQUEST_LOCATION_PERMISSION);
    }

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
    현재 위치를 가져온다.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                //Todo: 업데이트된 좌표로 마커 이동
               updateMarker();

            }
        };

    }

    private void updateMarker() {
        //TODO : permission 체크//이동 좌표 찍어보기
        Double latitude = mCurrentLocation.getLatitude();
        Double longtitude = mCurrentLocation.getLongitude();

        //update UI
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));


//        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        LOGD(TAG, "Lattitude : " + latitude + "/Longtitude : " + longtitude);

    }


}

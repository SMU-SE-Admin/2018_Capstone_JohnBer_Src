package smu.ac.kr.johnber.run;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
    private Location LastLocation;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_main);
        initView();
        seListeners();
        //TODO: 일정 시간마다 데이터를 갱신해야함

        mMapview.onCreate(savedInstanceState);
        //callback onMapReady
        mMapview.getMapAsync(this);
        //Initialize mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

    /*
    현재위치 확인 기능 활성화
    Permission 체크 및 요청
    */
        enableMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
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
        //Todo: 버튼 visibility
        mRun.setVisibility(view.GONE);
        //Todo: RunningActivity의 역할을 MainActiviy에서, fragmentTransition으로 바꾸기
        RunningFragment runningFragment = new RunningFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.homeContainer, runningFragment, "RUNNINGFRAGMENT")
                .addToBackStack(null)
                .commit();
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
                        PermissionUtil.makePermissionRationaleSnackbar(this, "앱 사용 동안 위치 권한 접근이 필요합니다. 설정>권한에서 권한을 허용할 수 있습니다.");
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;

        googleMap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    /**
     * 위치 권한 설정 및 지도 세팅
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtil.shouldAskPermission(this, PERMISSION)) {
            //권한 있음
            if (mgoogleMap != null)
                mgoogleMap.setMyLocationEnabled(true);
            return;
        }
        //권한 체크
        PermissionUtil.checkPermission(this, PERMISSION, REQUEST_LOCATION_PERMISSION);
    }

    //Todo: Location 지도 설정
    private void getLocationRequest() {
        @SuppressLint("RestrictedApi")
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*
    mFusedLocationClient로 부터 위치를 받았을 때 실행할 콜백 메소드
    현재 위치를 가져온다.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                //마커 업데이트?
//        updateUI();
            }
        };

    }

    private void updateUI() {
        //TODO : permission 체크
    }


}

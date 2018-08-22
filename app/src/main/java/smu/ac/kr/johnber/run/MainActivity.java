package smu.ac.kr.johnber.run;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.account.loginActivity;
import smu.ac.kr.johnber.course.CourseDetailFragment;
import smu.ac.kr.johnber.map.JBLocation;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.CourseRequest;
import smu.ac.kr.johnber.opendata.WeatherForecast;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.LocationUtil;
import smu.ac.kr.johnber.util.LogUtils;
import smu.ac.kr.johnber.util.PermissionUtil;
import smu.ac.kr.johnber.util.RecordUtil;

/**
 * 메인화면
 * - 위치, 네트워크. 퍼미션 세팅/ 체크
 * - 지도 설정
 * - 동네예보 주기적으로 싱크
 * - 현재 위치 표시
 * - 위도 경도값 알아내기
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnMapReadyCallback {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private Realm mRealm;
    private boolean isFindCourseBtClicked;
    private Button mRun;
    public SharedPreferences prefs;
    private FloatingActionButton mFindCourse;
    private MapView mMapview;
    private GoogleMap mgoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private FirebaseAuth mAuth; //FirebaseAuth 사용자 로그인 여부 확인 변수
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_main);
        initView();
        seListeners();
        mAuth = FirebaseAuth.getInstance();
        isFindCourseBtClicked = false;
    checkUserlogin();

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

//        WeatherForecast.loadWeatherData(this);

        Realm.init(this);
        RealmConfiguration config12 = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();
        mRealm.setDefaultConfiguration(config12);
        mRealm = Realm.getInstance(config12);

        LOGD(TAG, "onCreate");

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        checkFirstRun();

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
         mAuth.addAuthStateListener(mAuthListener);
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
         mAuth.removeAuthStateListener(mAuthListener);
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
        mFindCourse = findViewById(R.id.btn_find_course);
        mFindCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFindCourseBtClicked == true) {

                isFindCourseBtClicked = false;
                } else if (isFindCourseBtClicked == false) {
                    isFindCourseBtClicked = true;
                }
                findCourse();
            }
        });

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
            //현재위치좌표를 같이 넘겨줌
            intent.putExtra("latitude", mCurrentLocation.getLatitude());
            intent.putExtra("longitude", mCurrentLocation.getLongitude());
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
        mgoogleMap.getUiSettings().setZoomControlsEnabled(false);
        // Map style 바꾸기 - 한국은 현행법상 적용이안된다...
//        mgoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));
        if (PermissionUtil.shouldAskPermission(this, PERMISSION)) {
            //권한없는경우 default 서울로 설정 + 안내 문구
            googleMap.setMinZoomPreference(17);
            LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
            mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 18));
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
//        LOGD(TAG, "enableLocationTracking : check permission");
        if (!PermissionUtil.shouldAskPermission(this, PERMISSION)) {
            //권한 있음
            if (mgoogleMap != null) {
                mgoogleMap.setMyLocationEnabled(true);
//                LOGD(TAG, "Start location tracking");
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                return;
            }
        }

//        LOGD(TAG, "enableLocationTracking : have no permission");
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
        locationRequest.setInterval(5000);
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
               updateMarker();

            }
        };

    }

    private void updateMarker() {
        Double latitude = mCurrentLocation.getLatitude();
        Double longtitude = mCurrentLocation.getLongitude();

        //update UI
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if (isFindCourseBtClicked == false) {
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        }
//        lse if(isFindCourseBtClicked == true && mFusedLocationClient.getl)
//        LOGD(TAG, "Lattitude : " + latitude + "/Longtitude : " + longtitude);

    }

    //내 현재 위치를 중심으로 일정 반경 내의 코스를 지도에 마커로 표시
    private void findCourse() {
        //현재 위치 좌표 -> 시, 군
        // 시, Realm에서 검색
        // 필터링된 데이터들의 위,경도구하기
        // 현위치 좌표~검색된 좌표 거리계산
        // 반경 1km 내의 위치좌표만 표시

        Geocoder mGeoCoder = new Geocoder(getApplicationContext(), Locale.KOREA);
        List<Address> currentLocationName = null;
        String locality = null;
        ArrayList<LatLng> markerList = new ArrayList<>();

        try {
            currentLocationName = mGeoCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
            locality = currentLocationName.get(0).getLocality();
            LOGD(TAG,locality);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RealmResults<RunningCourse> query = filterResults(locality);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //각 코스의 출발지~도착치 모두 비교
        // integer : primary key of course
        HashMap<Integer, LatLng> courseMap = new HashMap<>();
        HashMap<Integer, String> courseName = new HashMap<>();
        final double QUERY_DIAMETER = 2.0; // km 단위

        LOGD(TAG,"query result : "+query.size());
        //현재 좌표와 검색결과 좌표 거리 계산
        if (query.size() !=0) {
            for (RunningCourse course : query) {

                if (RecordUtil.distance(course.getsLat(), course.getsLng()
                        , mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())
                        < QUERY_DIAMETER) {

                    courseMap.put(course.getId(), new LatLng(course.getsLat(), course.getsLng()));
                    courseName.put(course.getId(), course.getCourseName());
                }

                if (RecordUtil.distance(course.geteLat(), course.geteLng()
                        , mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())
                        < QUERY_DIAMETER) {

                    courseMap.put(course.getId(), new LatLng(course.geteLat(), course.geteLng()));
                    courseName.put(course.getId(), course.getCourseName());
                }

            }
        }
        //HACK:
        if (courseMap.size() > 0) {
            Iterator it = courseMap.entrySet().iterator();
            for ( ; it.hasNext(); ) {
                Map.Entry pair = (Map.Entry) it.next();

                MarkerOptions startMarker = new MarkerOptions();
                startMarker.position((LatLng) pair.getValue())
                        .title(courseName.get(pair.getKey()))
                        .snippet(String.valueOf(pair.getKey()))
                        .icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, this));
                mgoogleMap.addMarker(startMarker).showInfoWindow();
                builder.include((LatLng) pair.getValue());

            }

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding);
        mgoogleMap.moveCamera(cu);

        // 마커의 타이틀 누르면 해당 코스 정보 화면으로 이동
            //FIXME : 메인- 주변코스검색 - 상세페이지에서 백버튼 누르면 오류 : back pressed null
        mgoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //get marker's id : snippet = course id
                int position = Integer.parseInt(marker.getSnippet());
                Bundle data = new Bundle();
                //realm id는 1부터시작
                data.putInt("position", position-1);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CourseDetailFragment fragment = new CourseDetailFragment();
                fragment.setArguments(data);
                fragmentTransaction.add(R.id.homeContainer, fragment, "COURSEDETAILFRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        }

    }


    public RealmResults<RunningCourse> filterResults(String text) {
        RealmResults<RunningCourse> query = mRealm.where(RunningCourse.class)
                .contains("startPointRoadAddr",text)
                .or()
                .contains("startPointAddr",text)
                .or()
                .contains("endPointRoadAddr",text)
                .or()
                .contains("endPointAddr",text)
                .findAll();
        return query;
    }


    private List<LatLng> getLatLangFromAddr(RunningCourse mcourseData) {
        List<LatLng> latlng = new ArrayList<>();
        if(mcourseData == null)
            LOGD(TAG,"mcourseData is empty");
        try {
            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
            Geocoder mGeoCoder = new Geocoder(this, Locale.KOREA);
            List<Address> startLocation = null;
            List<Address> endLocation = null;
            if (!(mcourseData.getStartPointAddr().equals("null")||mcourseData.getStartPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
            } else if (!(mcourseData.getStartPointRoadAddr().equals("null")||mcourseData.getStartPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
            }

            if (!(mcourseData.getEndPointAddr().equals("null")||mcourseData.getEndPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());
                if (endLocation.size() <= 0) {
                    endLocation = startLocation;
                }
            } else if (!(mcourseData.getEndPointRoadAddr().equals("null")||mcourseData.getEndPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
            }
            LatLng start =new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude());
            LatLng end =new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude());
            latlng.add(start);
            latlng.add(end);
            LOGD(TAG, "size of lsitsts : " + latlng.size());


        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "cannot find location info" + mcourseData.getCourseName() + " " + mcourseData.getStartPointAddr() + " " + mcourseData.getStartPointRoadAddr() + " " + mcourseData.getEndPointAddr() + " " + mcourseData.getEndPointRoadAddr());
        }

        return latlng;
    }

 //로그인 여부 확인 함수.
  private void checkUserlogin(){
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


        } else {
          // User is signed out activity_login 화면으로 이동.
          Log.d(TAG, "onAuthStateChanged:signed_out");
          Intent intent = new Intent(getApplicationContext(), loginActivity.class);
          startActivity(intent);
        }
        // ...
      }
    };
  }

  //앱 최초 실행시 코스정보를 받아오기 위한 함수
  public void checkFirstRun() {
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
            LOGD(TAG, "THIS IS FIRSTRUN : "+ isFirstRun);
        if (isFirstRun) {
            new CourseRequest(getApplicationContext()).loadCourseData();
            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }


}

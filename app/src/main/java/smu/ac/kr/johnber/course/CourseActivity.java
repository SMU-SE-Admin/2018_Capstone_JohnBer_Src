package smu.ac.kr.johnber.course;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.SearchView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.CourseRequest;
import smu.ac.kr.johnber.run.MainActivity;
import smu.ac.kr.johnber.run.RunningFragment;

public class CourseActivity extends BaseActivity implements CourseViewHolder.itemClickListener {

    private final static String TAG = makeLogTag(CourseActivity.class);
    //TODO : 테스트용, api 데이터 다운로드 -> 기능 구현 후 앱 최초 실행 시 다운로드 하도록 구현할것
    private Button mButton;
    private Button mButton2;
    private Realm mRealm;
    public SharedPreferences prefs;
    CourseAdapter mAdapter;
    private SearchView mSearchView;
    private GeoDataClient mGeoDataClient;
    private AppBarLayout mAppBarLayout;
    private CourseDetailFragment.detailFragListener fragListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_act);
        LOGD(TAG, "onCreate");
        initView();
        mAppBarLayout = findViewById(R.id.appbar);
        //RecyclerView 설정
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        RealmResults<RunningCourse> courseItems = mRealm
                .where(RunningCourse.class).findAll();
        mGeoDataClient = Places.getGeoDataClient(this);


        mAdapter = new CourseAdapter(this, courseItems, true, false
                , this,mGeoDataClient);
        RealmRecyclerView recyclerView = (RealmRecyclerView) findViewById(R.id.rv_course);
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LOGD(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LOGD(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        LOGD(TAG, "onDestroy");
        mRealm.close();
    }


    @Override
    protected int getNavigationItemID() {
        return R.id.action_course;
    }

    private void initView() {
        mButton = findViewById(R.id.btn_apitest);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CourseRequest(getApplicationContext()).loadCourseData();
            }
        });
        mButton2 = findViewById(R.id.btn_apitest2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRealm.beginTransaction();
                final RealmResults<RunningCourse> results = mRealm.where(RunningCourse.class).findAll();
                results.deleteAllFromRealm();

                LOGD(TAG, "db has been cleared" + mRealm.where(RunningCourse.class).findAll().size());
                mRealm.commitTransaction();
            }
        });
        mSearchView = findViewById(R.id.sv_searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //filter recycler view
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // recyclerView 클릭 리스너
    @Override
    public void onItemClicked(View view, int position) {
//        LOGD(TAG, "CLICKED!" + position);
        //코스 detail fragment inflate
        mAppBarLayout.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                - mAppBarLayout.getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        mAppBarLayout.startAnimation(animate);
        showDeatilView(position, view);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                //filter recycler view
//                mAdapter.getFilter().filter(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                mAdapter.getFilter().filter(s);
//                return false;
//            }
//        });
//
//        return true;
//    }

    public void showDeatilView(int position,  View view) {
        fragListener = new CourseDetailFragment.detailFragListener() {
            @Override
            public void onBackPressed() {
                if (mAppBarLayout != null && mAppBarLayout.getVisibility() == View.GONE) {
                    mAppBarLayout.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,                 // fromXDelta
                            0,                 // toXDelta
                            -mAppBarLayout.getHeight(),                 // fromYDelta
                            0); // toYDelta
                    animate.setDuration(300);
                    animate.setFillAfter(true);
                    mAppBarLayout.startAnimation(animate);

                }
            }
        };
        Bundle data = new Bundle();
        data.putInt("position", position); //recycler view상의 포지션
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CourseDetailFragment fragment = new CourseDetailFragment();
        fragment.setListener(fragListener);
        fragment.setArguments(data);
        fragmentTransaction.add(R.id.course_item_container, fragment, "COURSEDETAILFRAGMENT")
                .addToBackStack(null)
                .commit();
    }


    private List<LatLng> getLatLangFromAddr(RunningCourse mcourseData) {
        List<LatLng> latlng = new ArrayList<>();
        if (mcourseData == null)
            LOGD(TAG, "mcourseData is empty");
        try {
            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
            Geocoder mGeoCoder = new Geocoder(this, Locale.KOREA);
            List<Address> startLocation = null;
            List<Address> endLocation = null;
            if (!(mcourseData.getStartPointAddr().equals("null") || mcourseData.getStartPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
            } else if (!(mcourseData.getStartPointRoadAddr().equals("null") || mcourseData.getStartPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
            }

            if (!(mcourseData.getEndPointAddr().equals("null") || mcourseData.getEndPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());
                if (endLocation.size() <= 0) {
                    endLocation = startLocation;
                }
            } else if (!(mcourseData.getEndPointRoadAddr().equals("null") || mcourseData.getEndPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
            }
            LatLng start = new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude());
            LatLng end = new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude());
            latlng.add(start);
            latlng.add(end);
            LOGD(TAG, "size of lsitsts : " + latlng.size());


        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "cannot find location info " + mcourseData.getCourseName() + " " + mcourseData.getStartPointAddr() + " " + mcourseData.getStartPointRoadAddr() + " " + mcourseData.getEndPointAddr() + " " + mcourseData.getEndPointRoadAddr());
        }

        return latlng;
    }


}

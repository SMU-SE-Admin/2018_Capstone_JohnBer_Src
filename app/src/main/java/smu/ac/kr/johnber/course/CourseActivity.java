package smu.ac.kr.johnber.course;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.CourseRequest;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class CourseActivity extends BaseActivity implements CourseViewHolder.itemClickListener, CourseAdapter.filteringlistener {

    private final static String TAG = makeLogTag(CourseActivity.class);

    private Button mButton;
    private Button mButton2;
    private AppBarLayout mAppBarLayout;
    private SearchView mSearchView;
    private Toolbar toolbar;

    private Realm mRealm;
    private RealmResults<RunningCourse> courseItems;
    private CourseAdapter mAdapter;

    private GeoDataClient mGeoDataClient;
    private CourseDetailFragment.detailFragListener fragListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LOGD(TAG, "onCreate");
        setContentView(R.layout.course_act);
        initView();
        mAppBarLayout = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.getRootView().findViewById(R.id.tb_logo).setVisibility(View.GONE);

        //RecyclerView 설정
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        courseItems = mRealm
                .where(RunningCourse.class).findAll();
        mGeoDataClient = Places.getGeoDataClient(this);

        mAdapter = new CourseAdapter(this, courseItems, true, false
                , this, mGeoDataClient, this);
        RealmRecyclerView recyclerView = findViewById(R.id.rv_course);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        LOGD(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LOGD(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LOGD(TAG, "onPause");
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
    }

    // recyclerView 클릭 리스너
    @Override
    public void onItemClicked(View view, int position) {
        mAppBarLayout.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                -mAppBarLayout.getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        mAppBarLayout.startAnimation(animate);
        showDeatilView(position, view);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        ImageView icon = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(getResources().getColor(R.color.black));
        ImageView icon2 = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        icon2.setColorFilter(getResources().getColor(R.color.black));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String charString) {

                //filter recycler view
                mAdapter.getFilter().filter(charString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.getFilter().filter("reset");
                return true;
            }
        });
        return true;
    }


    @Override
    public void onFiltered(RealmResults<RunningCourse> filteredResult) {
        courseItems = filteredResult;
        mAdapter.notifyDataSetChanged();
    }

    public void showDeatilView(int position, View view) {
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
        position = courseItems.get(position).getId() - 1;
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

}

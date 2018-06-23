package smu.ac.kr.johnber.course;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.CourseRequest;
import smu.ac.kr.johnber.run.RunningFragment;

public class CourseActivity extends BaseActivity implements CourseViewHolder.itemClickListener {

  private final static String TAG = makeLogTag(CourseActivity.class);
  //TODO : 테스트용, api 데이터 다운로드 -> 기능 구현 후 앱 최초 실행 시 다운로드 하도록 구현할것
  private Button mButton;
  private Button mButton2;
  private Realm mRealm;
  public SharedPreferences prefs;
  private CourseViewHolder.itemClickListener listener;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.course_act);
    LOGD(TAG, "onCreate");
    initView();

    //RecyclerView 설정
    Realm.init(this);
   RealmConfiguration config6 = new RealmConfiguration.Builder()
           .deleteRealmIfMigrationNeeded()
           .build();
    mRealm.setDefaultConfiguration(config6);
    mRealm = Realm.getInstance(config6);
    RealmResults<RunningCourse> courseItems = mRealm
            .where(RunningCourse.class).findAll();
    CourseAdapter adapter = new CourseAdapter(this, courseItems, true, false
            , this);
    prefs = getSharedPreferences("Pref", MODE_PRIVATE);
    checkFirstRun();
    RealmRecyclerView recyclerView = (RealmRecyclerView)findViewById(R.id.rv_course);
    recyclerView.setAdapter(adapter);

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

private void initView(){
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

      LOGD(TAG,"db has been cleared"+mRealm.where(RunningCourse.class).findAll().size());
      mRealm.commitTransaction();
    }
  });
}

// recyclerView 클릭 리스너
  @Override
  public void onItemClicked(View view, int position) {
    LOGD(TAG,"CLICKED!"+position);
    //코스 detail fragment inflate
    showDeatilView(position,view);


  }

  public void showDeatilView(int position, View view) {
    Bundle data = new Bundle();
    data.putInt("position", position);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    CourseDetailFragment fragment = new CourseDetailFragment();
    fragment.setArguments(data);
    fragmentTransaction.add(R.id.course_item_container,fragment,"COURSEDETAILFRAGMENT")
            .addToBackStack(null)
            .commit();
  }

  public void checkFirstRun(){
    boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
    if(isFirstRun)
    {
      new CourseRequest(getApplicationContext()).loadCourseData();
      prefs.edit().putBoolean("isFirstRun",false).apply();
    }
  }
}

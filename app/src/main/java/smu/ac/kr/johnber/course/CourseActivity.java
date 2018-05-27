package smu.ac.kr.johnber.course;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

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

public class CourseActivity extends BaseActivity {

  private final static String TAG = makeLogTag(CourseActivity.class);
  //TODO : 테스트용, api 데이터 다운로드 -> 기능 구현 후 앱 최초 실행 시 다운로드 하도록 구현할것
  private Button mButton;
  private Button mButton2;
  private Realm mRealm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.course_act);
    LOGD(TAG, "onCreate");
    initView();
    //RecyclerView 설정
    Realm.init(this);
   RealmConfiguration config = new RealmConfiguration.Builder()
            .build();
    mRealm = Realm.getInstance(config);
    RealmResults<RunningCourse> courseItems = mRealm
            .where(RunningCourse.class).findAll();
    CourseAdapter adapter = new CourseAdapter(this, courseItems, true, false);
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
  //테스트
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

}

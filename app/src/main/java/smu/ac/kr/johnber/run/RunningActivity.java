package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smu.ac.kr.johnber.R;

/**
 * A simple {@link Fragment} subclass.
 */
//Todo: PauseRunningFragment에서 return누르는경우... Interface를 구현하여, 이벤트 발생시 운동기록을 db에 저장하고(백그라운드서비스) MainActivity로 전환할것

/**
 * - 달리기 종료 후 데이터 저장
 * - fragment간 통신 구현
 *
 */
public class RunningActivity extends AppCompatActivity {

  private final static String TAG = makeLogTag(RunningActivity.class);


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.run_running_activity);
    initView();
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
  }
  public void initView() {
    // 달리기 fragment
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.run_running_status_container,new RunningFragment(),"RUNNINGFRAGMENT")
            .addToBackStack(null)
            .commit();

  }

}

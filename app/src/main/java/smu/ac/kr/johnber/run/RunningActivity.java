package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class RunningActivity extends AppCompatActivity implements PauseRunningFragment.RunFragCallBackListener {

  private final static String TAG = makeLogTag(RunningActivity.class);
  private Record mRecord;
  private Fragment runningFragment;

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




  public void setRecord(Record record) {
    mRecord = record;
  }

  //Resume버튼을 눌렀을 때 state를 Resume으로 바꾸고, runningFragment에서 start trackerService를 한다.
  @Override
  public void onClickedResume() {
    LOGD(TAG,"onClikedReusme");
    FragmentManager fragmentManager = getSupportFragmentManager();
    RunningFragment runfrag = (RunningFragment) fragmentManager.findFragmentByTag("RUNNINGFRAGMENT");
    runfrag.setState(20003);
   runfrag.resumebindService();
  }

  /**
   * sharedPreference에 있는 데이터 + date, endTime, Title -> Record객체에 저장
   * firebase에 저장
   */
  @Override
  public void onClickedReturn() {
    LOGD(TAG,"onClikedReturn ~ save data");

    SharedPreferences preferences;
    preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);

    Gson gson = new Gson();
    String response = preferences.getString("LOCATIONLIST", "");
      ArrayList<Location> locationArrayList = gson.fromJson(response, new TypeToken<List<Location>>(){}.getType());

    // 나머지 복원
    double distance = Double.parseDouble(preferences.getString("DISTANCE", ""));
    double elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", ""));
    double calories = Double.parseDouble(preferences.getString("CALORIES", ""));
    double startTime = Double.parseDouble(preferences.getString("STARTTIME", ""));

    //TODO : date, endTime, Title 받아오기
    Date date = null;
    double endTime = 0;
    String title = null;   // date를 변환해서 우선 넣기로함

    mRecord = new Record(distance, elapsedTime, calories, locationArrayList, date, startTime, endTime, title);
    //TODO : 파이어베이스와 연동
  }


}

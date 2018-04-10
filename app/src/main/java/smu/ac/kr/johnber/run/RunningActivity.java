package smu.ac.kr.johnber.run;


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
//Todo: MAPVIEW 구현

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

  public void initView() {
    // 달리기 fragment 동적 추가
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.run_running_status_container,new RunningFragment(),"RUNNINGFRAGMENT");
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();

  }

}

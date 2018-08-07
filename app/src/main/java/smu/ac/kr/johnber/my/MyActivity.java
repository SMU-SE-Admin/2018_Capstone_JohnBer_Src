package smu.ac.kr.johnber.my;

import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;

public class MyActivity extends BaseActivity {
  private final static String TAG = makeLogTag(MyActivity.class);


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.my_act);

    MyStatisticsPagerAdapter myStatisticsPagerAdapter = new MyStatisticsPagerAdapter();
    ViewPager myStatisticsviewPager = findViewById(R.id.my_statistics_viewPager);
    myStatisticsviewPager.setAdapter(myStatisticsPagerAdapter);

    //        set on page listener 구현?


  }


  @Override
  protected int getNavigationItemID() {
    return R.id.action_statistics;
  }
}

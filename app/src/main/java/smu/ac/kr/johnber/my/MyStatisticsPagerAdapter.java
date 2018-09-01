package smu.ac.kr.johnber.my;

import static android.provider.Settings.Global.getString;
import static android.support.constraint.Constraints.TAG;
import static android.util.Config.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.RecordUtil;

/**
 * Created by yj34 on 26/03/2018.
 */

public class MyStatisticsPagerAdapter extends PagerAdapter {
  /**
   *
   * @return number of pages
   * 일별 주별 월별 통계 레이아웃을 구현
   */
  /**
   *
   * @return
   */
  private View mView;
  private List<Double> mStats ;
  private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
  private Context mContext;

  public MyStatisticsPagerAdapter(Context context, HashMap<String, Record> recordHashMap) {
    super();
    this.mContext = context;
    this.recordHashMap = recordHashMap;
  }

  public void setmView(ViewPager view) {
    view.setAdapter(this);
  }
  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    View mView = LayoutInflater.from(mContext).inflate(R.layout.my_statistics_daily_view, null);

    switch (position) {
      case 0:
        mStats = DailyStatistics.dailyStats(recordHashMap);
        break;
      case 1:
        mStats = WeeklyStatistics.weeklyStats(recordHashMap);
      case 2:
        mStats = MonthlyStatistics.monthlyStats(recordHashMap);
    }
    //set data
    TextView km = mView.findViewById(R.id.tv_statistics_daily_km);
    TextView time =  mView.findViewById(R.id.tv_statistics_daily_time);
    TextView cal = mView.findViewById(R.id.tv_statistics_daily_calories);
    km.setText(mStats.get(2).toString());
    String stime = RecordUtil.milliseconsToStringFormat(mStats.get(0));
    time.setText(stime);
    cal.setText(mStats.get(1).toString());
  container.addView(mView);
    return mView;
  }



  public void getRecord(){


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        //DB에 저장된 데이터 HashMap에 저장.
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          //DB에서 로그인한 아이디와 일치하는지 확인 후 해당 데이터만 읽어옴.
          if (snapshot.getKey().toString().equals(uid)) {
            //Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
            for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
              if (recordSnapshot.getKey().toString().equals("userRecord")) {
                String keyDate1 = "";
                for (DataSnapshot snapshot1 : recordSnapshot.getChildren()) {
                  keyDate1 = snapshot1.getKey().toString();
                  for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                    String keyDate = keyDate1 + '/' + snapshot2.getKey().toString();
                    recordHashMap.put(keyDate, snapshot2.getValue(Record.class));
                    //Log.d("mainactivity", "hashMap"+recordHashMap.toString());
                  }
                }
              }
            }
          }
        }

////        DailyStatistics.dailyStats(recordHashMap);
//        WeeklyStatistics.weeklyStats(recordHashMap);
////        WeeklyStatistics;
//        MonthlyStatistics.monthlyStats(recordHashMap);
//
//        DailyStatistics daily = new DailyStatistics();
//        daily.dailyStats(recordHashMap);
//        mStats = daily.getStats();
//        LOGD(TAG,"he"+ mStats.toString());
//
//
//        TextView km = mView.findViewById(R.id.tv_statistics_daily_km);
//        TextView time =  mView.findViewById(R.id.tv_statistics_daily_time);
//        TextView cal = mView.findViewById(R.id.tv_statistics_daily_calories);
//        km.setText(String.valueOf(mStats.get(0)));
//        time.setText(String.valueOf(mStats.get(1)));
//        cal.setText(String.valueOf(mStats.get(2)));

      }


      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View)object);
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {

    return view == object;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return "Daily";
      case 1:
        return "Weekly";
      case 2:
        return "Monthly";
    }
    return super.getPageTitle(position);
  }


  /*
  //JGH
  public void dailyStats(HashMap<String, Record> recordHashMap) {
    //현재 시간 받아오기.
    long now = System.currentTimeMillis();
    Date date = new Date(now);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String todayDate = sdf.format(date);

    List<Double> dailyKM = new ArrayList<Double>();
    List<Double> dailyTime = new ArrayList<Double>();
    List<Double> dailyCal = new ArrayList<Double>();


    Iterator<String> iter = recordHashMap.keySet().iterator();
    while(iter.hasNext()){
      //Log.d("mainactivity", "dkdkdkdkdkdkdkd : " + recordHashMap.keySet());
      String keys = (String) iter.next();
      String dates = keys.split("/")[0];
      String times = keys.split("/")[1];

      //Log.d("mainactivity", "keys, dates, times: " + dates.toString());


      if (todayDate.equals(dates)){
        Record dailyRecord = recordHashMap.get(keys);

        dailyKM.add(dailyRecord.getDistance());
        dailyTime.add(dailyRecord.getElapsedTime());
        dailyCal.add(dailyRecord.getCalories());

        Log.d("mainactivity", "daily: " + dailyKM.toString());
      }
    }
  */

}

package smu.ac.kr.johnber.my;

import static android.provider.Settings.Global.getString;
import static android.util.Config.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

  private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);

    int viewId = 0;

    switch (position) {
      case 0:
        viewId = R.layout.my_statistics_daily_view;
        getRecord();
        break;
      case 1:
        viewId = R.layout.my_statistics_weekly_view;
        break;
      case 2:
        viewId = R.layout.my_statistics_monthly_view;
        break;
    }

    View view = inflater.inflate(viewId, null);
    container.addView(view,0);
    return view;

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
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
          //DB에서 로그인한 아이디와 일치하는지 확인 후 해당 데이터만 읽어옴.
          if (snapshot.getKey().toString().equals(uid)){
            //Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
            for(DataSnapshot recordSnapshot : snapshot.getChildren()){
              if (recordSnapshot.getKey().toString().equals("userRecord")){
                String keyDate1 = "";
                for (DataSnapshot snapshot1 : recordSnapshot.getChildren()){
                  keyDate1 = snapshot1.getKey().toString();
                  for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                    String keyDate = keyDate1 + '/' + snapshot2.getKey().toString();
                    recordHashMap.put(keyDate, snapshot2.getValue(Record.class));
                    //Log.d("mainactivity", "hashMap"+recordHashMap.toString());
                  }
                }
              }
            }
          }
        }
        dailyStats(recordHashMap);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
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
      //ex) keys : hashmap의 key, 2018
      String keys = (String) iter.next();
      String dates = keys.split("/")[0];
      String times = keys.split("/")[1];

      //Log.d("mainactivity", "keys, dates, times: " + dates.toString());


      if (todayDate.equals(dates)){
        Record dailyRecord = recordHashMap.get(keys);

        dailyKM.add(dailyRecord.getDistance());
        dailyTime.add(dailyRecord.getElapsedTime());
        dailyCal.add(dailyRecord.getCalories());

        //Log.d("mainactivity", "daily: " + dailyKM.toString());


        calculateTime(dailyTime);
      }
    }
  }

  public void calculateKcal(List<Double> kcalList){
    double sum=0;
    for(int i=0; i<kcalList.size(); i++){
      sum+=kcalList.get(i);
    }
    Log.d("MAINACTIVITY", "kcal" + sum);

  }

  public void calculateTime(List<Double> timeList){
    double sum =0;
    for(int i=0; i<timeList.size(); i++){
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      sum += timeList.get(i);
    }

    int seconds = (int) (sum / 1000) % 60 ;            //초
    int minutes = (int) ((sum/ (1000*60)) % 60);  //분
    int hours   = (int) ((sum / (1000*60*60)) % 24);//시

    Log.d("MAINACTIVITY", "TIME : " + String.format("%02d h:%02d m:%02d s", hours, minutes, seconds));
  }

  public void calculateKM(List<Double> kmList){
    double sum=0;
    for(int i=0; i<kmList.size(); i++){
      sum+=kmList.get(i);
    }
    Log.d("MAINACTIVITY", "KM" + sum);
  }

}

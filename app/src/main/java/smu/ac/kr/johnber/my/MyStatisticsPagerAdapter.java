package smu.ac.kr.johnber.my;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.RecordUtil;

/**
 * Created by yj34 on 26/03/2018.
 */

public class MyStatisticsPagerAdapter extends PagerAdapter {
  /**
   * @return number of pages
   * 일별 주별 월별 통계 레이아웃을 구현
   */
  /**
   * @return
   */
  private List<Double> mStats ;
  private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
  private Context mContext;

  public MyStatisticsPagerAdapter(Context context, HashMap<String, Record> recordHashMap) {
    super();
    this.mContext = context;
    this.recordHashMap = recordHashMap;
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
}
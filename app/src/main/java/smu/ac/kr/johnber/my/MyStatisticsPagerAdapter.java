package smu.ac.kr.johnber.my;

import static android.provider.Settings.Global.getString;

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

import java.util.ArrayList;
import java.util.HashMap;
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
}

package smu.ac.kr.johnber.my;

import static android.provider.Settings.Global.getString;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import smu.ac.kr.johnber.R;

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

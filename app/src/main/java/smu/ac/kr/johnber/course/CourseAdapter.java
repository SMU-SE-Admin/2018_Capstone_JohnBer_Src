package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseViewHolder> {

  private final static String TAG = makeLogTag(CourseAdapter.class);

  @Override
  public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(CourseViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }
}

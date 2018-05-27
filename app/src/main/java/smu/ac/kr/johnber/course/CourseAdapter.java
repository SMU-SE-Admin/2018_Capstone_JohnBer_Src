package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RealmBasedRecyclerViewAdapter<RunningCourse, CourseViewHolder> {

  private final static String TAG = makeLogTag(CourseAdapter.class);
  private RealmResults<RunningCourse> data;

  public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                       boolean animateResults) {
      super(context, data, automaticUpdate, animateResults);
      this.data  = data;
  }

  @Override

  public CourseViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.course_item_collapsed, parent, false);
    CourseViewHolder viewHolder = new CourseViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindRealmViewHolder(CourseViewHolder holder, int position) {
      final RunningCourse courseItem = data.get(position);
      LOGD(TAG,courseItem.getCourseName());
      holder.courseName.setText(courseItem.getCourseName());
      holder.startPoint.setText(courseItem.getStartPoint());
      //TODO : 시간, 거리, 칼로리 계싼
  }

  @Override
  public int getItemCount() {
      return data.size();
  }
}

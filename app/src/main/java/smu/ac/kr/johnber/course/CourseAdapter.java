package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RealmBasedRecyclerViewAdapter<RunningCourse, CourseViewHolder>{

  private final static String TAG = makeLogTag(CourseAdapter.class);
  private RealmResults<RunningCourse> data;
  private CourseViewHolder.itemClickListener listener;

  public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                       boolean animateResults,CourseViewHolder.itemClickListener listener) {
      super(context, data, automaticUpdate, animateResults);
      this.data  = data;
      this.listener = listener;
//      Realm realm = Realm.getDefaultInstance();
//      this.data= realm
//              .where(RunningCourse.class).not()
//              .beginGroup().equalTo("distance", "null").and().equalTo("time","null").endGroup()
//              .sort("length", Sort.DESCENDING ).findAllAsync();

  }

  @Override

  public CourseViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.course_item_collapsed, parent, false);
    CourseViewHolder viewHolder = new CourseViewHolder(view, listener);
    return viewHolder;
  }

  @Override
  public void onBindRealmViewHolder(CourseViewHolder holder, int position) {
      final RunningCourse courseItem = data.get(position);
      holder.courseName.setText(courseItem.getCourseName());
      holder.startPoint.setText(courseItem.getStartPoint());
      holder.distance.setText(courseItem.getDistance());
      holder.time.setText(courseItem.getTime());

  }

  @Override
  public int getItemCount() {
      LOGD(TAG,"item size :  "+data.size());
      return data.size();
  }

}

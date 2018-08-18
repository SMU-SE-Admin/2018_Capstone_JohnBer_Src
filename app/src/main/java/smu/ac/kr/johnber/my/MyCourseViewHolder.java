package smu.ac.kr.johnber.my;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.course.CourseViewHolder;

import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class MyCourseViewHolder extends RecyclerView.ViewHolder{
  private final static String TAG = makeLogTag(MyCourseViewHolder.class);
  public TextView courseName;
  public TextView startPoint;
  public TextView distance;
  public TextView calories;
  public TextView time;
  public ImageView thumnail;
  public TextView KM;
  public TextView TIME;
  public TextView CAL;
  private MyCourseViewHolder.itemClickListener listener;

  public MyCourseViewHolder(View view, final MyCourseViewHolder.itemClickListener listener) {
    super(view);

    courseName = view.findViewById(R.id.tv_course_name);
    startPoint = view.findViewById(R.id.tv_cousrse_start_point);
    distance = view.findViewById(R.id.tv_course_distance);
    calories = view.findViewById(R.id.tv_course_calories);
    time = view.findViewById(R.id.tv_course_time);
    thumnail = view.findViewById(R.id.iv_course_map_thumbnail);

    CAL = view.findViewById(R.id.tv_course_time_text);
    KM = view.findViewById(R.id.tv_cousrse_km_text);
    TIME = view.findViewById(R.id.tv_course_calories_text);
    this.listener = listener;
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        listener.onItemClicked(view, getAdapterPosition());
      }
    });

  }

  public interface itemClickListener {
    public void onItemClicked(View view, int position);
  }



}

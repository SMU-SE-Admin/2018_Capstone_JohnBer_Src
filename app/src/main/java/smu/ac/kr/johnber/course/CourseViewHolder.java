package smu.ac.kr.johnber.course;

import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.RealmViewHolder;
import smu.ac.kr.johnber.R;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseViewHolder extends RealmViewHolder {
  private final static String TAG = makeLogTag(CourseViewHolder.class);

  public TextView courseName;
  public TextView startPoint;
  public TextView distance;
  public TextView calories;
  public TextView time;
  public TextView lbDistance;
  public TextView lbTime;
  public TextView lbCalories;
  //TODO : thumnail view  설정
  public ImageView thumnail;
  private itemClickListener listener;

  public CourseViewHolder(View view, final itemClickListener listener) {
    super(view);
    courseName = view.findViewById(R.id.tv_course_name);
    startPoint = view.findViewById(R.id.tv_cousrse_start_point);
    distance = view.findViewById(R.id.tv_course_distance);
    calories = view.findViewById(R.id.tv_course_calories);
    time = view.findViewById(R.id.tv_course_time);
//    lbDistance = view.findViewById(R.id.tv_cousrse_km_text);
//    lbTime = view.findViewById(R.id.tv_course_time_text);
//    lbCalories = view.findViewById(R.id.tv_course_calories_text);
    thumnail = view.findViewById(R.id.iv_course_map_thumbnail);
    setLabel();

    this.listener = listener;
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        listener.onItemClicked(view, getAdapterPosition());
      }
    });
  }

  public void setLabel() {
//    lbTime.setText("TIME");
//    lbDistance.setText("KM");
//    lbCalories.setText("CAL");
  }

  public interface itemClickListener {
    public void onItemClicked(View view, int position);
  }

}

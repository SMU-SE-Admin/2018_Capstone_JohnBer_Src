package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.zip.Inflater;

import io.realm.Realm;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;

/**
 * A simple {@link Fragment} subclass.
 */

//Todo : MAPVIEW setting
public class CourseDetailFragment extends Fragment {
  private final static String TAG = makeLogTag(CourseDetailFragment.class);
  private TextView courseName;
  public TextView startPoint;
  public TextView endPoint;
  public TextView distance;
  public TextView calories;
  public TextView time;
  public TextView course;
  public TextView courseInfo;
  private Realm mRealm;
  private int dataNO;
  public CourseDetailFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
       Bundle savedInstanceState) {
    LOGD(TAG,"onCreateView");
    View view = inflater.inflate(R.layout.course_detail_fragment,container,false);

    Realm.init(getActivity().getApplicationContext());
    mRealm = Realm.getDefaultInstance();
    dataNO = getArguments().getInt("position");

    courseName = view.findViewById(R.id.tv_course_detail_summary_title);
    startPoint = view.findViewById(R.id.tv_course_detail_summary_start_point);
    endPoint = view.findViewById(R.id.tv_course_detail_summary_end_point);
    distance = view.findViewById(R.id.tv_course_distance);
    calories = view.findViewById(R.id.tv_course_calories);
    time = view.findViewById(R.id.tv_course_time);
    course = view.findViewById(R.id.tv_course_detail_info);
    courseInfo= view.findViewById(R.id.tv_course_detail_info_content);
    RunningCourse courseData = mRealm.where(RunningCourse.class).findAll().get(dataNO);
    courseName.setText(courseData.getCourseName());

    course.setText(courseData.getCourse());
    courseInfo.setText(courseData.getCourseInfo());
    startPoint.setText(courseData.getStartPoint());
    endPoint.setText(courseData.getEndPoint());

    return view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mRealm.close();

  }
}

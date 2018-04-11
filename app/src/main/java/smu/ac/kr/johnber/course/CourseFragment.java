package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import smu.ac.kr.johnber.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {
  private final static String TAG = makeLogTag(CourseFragment.class);

  public CourseFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.course_content_frag, container, false);

    return view;
  }

}

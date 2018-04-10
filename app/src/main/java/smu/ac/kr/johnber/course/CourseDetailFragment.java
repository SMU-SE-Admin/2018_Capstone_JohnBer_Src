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

//Todo : MAPVIEW setting
public class CourseDetailFragment extends Fragment {
  private final static String TAG = makeLogTag(CourseDetailFragment.class);


  public CourseDetailFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    TextView textView = new TextView(getActivity());
    textView.setText(R.string.hello_blank_fragment);
    return textView;
  }

}

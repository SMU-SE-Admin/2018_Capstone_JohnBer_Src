package smu.ac.kr.johnber.my;


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
public class MyCourseMapFragment extends Fragment {
  private final static String TAG = makeLogTag(MyCourseMapFragment.class);

//TODO MAPVIEW로???
  public MyCourseMapFragment() {
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

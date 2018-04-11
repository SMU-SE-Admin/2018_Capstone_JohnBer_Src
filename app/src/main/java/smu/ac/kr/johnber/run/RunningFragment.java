package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import smu.ac.kr.johnber.R;

/**
 * - MAP view 설정힐갓
 * - 버튼 listener, actionㅓㄹ정
 * - 위치 트래킹
 * - 경로좌표 저장
 * - 지도에 라인으로 표시
 * - 기록 측정
 * - UI View 업데이트
 */
public class RunningFragment extends Fragment {

  private final static String TAG = makeLogTag(RunningFragment.class);

  public RunningFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.run_running_fragment,container,false);
    return view;
  }

}

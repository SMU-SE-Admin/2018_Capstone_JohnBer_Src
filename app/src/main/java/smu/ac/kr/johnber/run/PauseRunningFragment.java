package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import smu.ac.kr.johnber.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PauseRunningFragment extends Fragment implements View.OnClickListener{
  private final static String TAG = makeLogTag(PauseRunningFragment.class);

  private MapView mMapView;
  private TextView mDistance;
  private TextView mTime;
  private TextView mCalories;
  private Button mResume;
  private Button mStop;
  private Button mReturn;
  private ViewGroup transitionContainer;
  private View mView;

  private RunFragCallBackListener callBackListener;

  public PauseRunningFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mView = inflater.inflate(R.layout.run_pause_running_fragment, container, false);
    initView();
    return mView;
  }
  @Override
  public void onAttach(Context context) {
    //Activity에 할당되었을 때 호출
    super.onAttach(context);
    // Activity로 데이터를 전달 할 커스텀 리스너 연결(RunningFragment와 통신을 위해 Activity를 거쳐 통신함)
    callBackListener = (RunFragCallBackListener) getActivity();
    LOGD(TAG,"onAttached");
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LOGD(TAG, "onActivityCreated");
    //Fragment 화면 full screen
  }

  @Override
  public void onPause() {
    //fragment를 떠났을 때 유지시킬 데이터 저장
    super.onPause();
    LOGD(TAG,"onPause");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOGD(TAG, "onDestroy");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    LOGD(TAG, "onDetach");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  public void initView() {
    mMapView = mView.findViewById(R.id.running_mapview);
    mDistance = mView.findViewById(R.id.tv_run_time);
    mTime = mView.findViewById(R.id.tv_run_time);
    mCalories = mView.findViewById(R.id.tv_run_calories);
    mResume = mView.findViewById(R.id.btn_resume);
    mStop = mView.findViewById(R.id.btn_stop);
    mReturn = mView.findViewById(R.id.btn_return);
    transitionContainer = mView.findViewById(R.id.run_running_status_container);
    mResume.setOnClickListener(this);
    mStop.setOnClickListener(this);
    mReturn.setOnClickListener(this);
  }


  @Override
  public void onClick(View view) {

    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

    switch (view.getId()) {
      case R.id.btn_resume:
        //달리기 재개
        if (fragmentManager.getBackStackEntryCount() != 0) {
          fragmentManager.popBackStack();
          callBackListener.onClickedResume();
        }
        break;

      case R.id.btn_stop:
        //return 버튼으로 transit
        TransitionManager
                .beginDelayedTransition(transitionContainer);
        showReturnButton(view);
        break;

      case R.id.btn_return:
        //Todo : RunningActivity에서 운동기록 정보를 저장 ->  on Ready callback 메소드 필요
        //Todo : 달리기 종료버튼 클릭 후 running fragment , pause fragment 모두 스택에서 pop
        callBackListener.onClickedReturn();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().onBackPressed();
        break;
    }
  }

  public void showReturnButton(View view){
    mResume.setVisibility(view.GONE);
    mStop.setVisibility(view.GONE);
    mReturn.setVisibility(view.VISIBLE);
  }

  public interface RunFragCallBackListener {
    public void onClickedResume();

   public void onClickedReturn();



}
}

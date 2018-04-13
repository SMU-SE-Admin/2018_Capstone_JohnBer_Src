package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import org.w3c.dom.Text;

import smu.ac.kr.johnber.R;

/**
 * - MAP view 설정 , 콜백 메소드
 * - 버튼 listener, actionㅓㄹ정
 * - 위치 트래킹
 * - 경로좌표 저장
 * - 지도에 라인으로 표시
 * - 기록 측정
 * - UI View 업데이트
 */
public class RunningFragment extends Fragment implements View.OnClickListener {

  private final static String TAG = makeLogTag(RunningFragment.class);

  private MapView mMapView;
  private TextView mDistance;
  private TextView mTime;
  private TextView mCalories;
  private Button mPauseButton;
  private View mView;
  private Button mRun;
  private Toolbar mToolbar;
  private BottomNavigationView mBottomNav;
  private View mWeatherWidgetView;

  public RunningFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.run_running_fragment, container, false);
    if(savedInstanceState == null) initView();

    return mView;
  }

  @Override
  public void onAttach(Context context) {
    //Activity에 할당되었을 때 호출
    super.onAttach(context);
    LOGD(TAG,"onAttached");
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LOGD(TAG, "onActivityCreated");

    //Fragment 화면 full screen으로 만듬
    hideActivityContainer();
  }

  @Override
  public void onPause() {
    //fragment를 떠났을 때 유지시킬 데이터 저장
    super.onPause();
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
    mPauseButton = mView.findViewById(R.id.btn_pause);

    mPauseButton.setOnClickListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOGD(TAG, "onDestroy");
    showActivityContainer();
  }

  @Override
  public void onClick(View view) {
    //달리기 중지 fragment
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.homeContainer, new PauseRunningFragment(), "PAUSEFRAGMENT")
            .addToBackStack(null)
            .commit();
  }
  private void hideActivityContainer() {
    mRun = getActivity().findViewById(R.id.btn_run);
    mToolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
    mBottomNav = getActivity().findViewById(R.id.bottomNavigationView);
    mWeatherWidgetView = getActivity().findViewById(R.id.v_weatherWidget_container);

    mRun.setVisibility(View.GONE);
    mToolbar.setVisibility(View.GONE);
    mBottomNav.setVisibility(View.GONE);
    mWeatherWidgetView.setVisibility(View.GONE);
  }

  private void showActivityContainer() {
    mRun.setVisibility(View.VISIBLE);
    mToolbar.setVisibility(View.VISIBLE);
    mBottomNav.setVisibility(View.VISIBLE);
    mWeatherWidgetView.setVisibility(View.GONE);
  }
}
package smu.ac.kr.johnber.run;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.account.loginActivity;
import smu.ac.kr.johnber.opendata.WeatherForecast;
import smu.ac.kr.johnber.util.LogUtils;

/**
 * 메인화면
 * - 위치, 네트워크. 퍼미션 세팅/ 체크
 * - 지도 설정
 * - 동네예보 주기적으로 싱크
 * - 현재 위치 표시
 * - 위도 경도값 알아내기
 */
//TODO: 지도 처리
public class MainActivity extends BaseActivity implements OnClickListener {

  private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

  private TextView mRegion;
  private TextView mSky;
  private TextView mCelcius;

  private Button mRun;


  //FirebaseAuth 사용자 로그인 여부 확인 변수
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_run_main);

    mAuth = FirebaseAuth.getInstance();

    initView();
    seListeners();

    //로그인 여부 확인
    checkUserlogin();

    //TODO: 일정 시간마다 데이터를 갱신해야함
    WeatherForecast.loadWeatherData(this);
    LOGD(TAG, "onCreate");

  }

  //로그인 여부 확인 함수.
  private void checkUserlogin(){
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


        } else {
          // User is signed out activity_login 화면으로 이동.
          Log.d(TAG, "onAuthStateChanged:signed_out");
          Intent intent = new Intent(getApplicationContext(), loginActivity.class);
          startActivity(intent);
        }
        // ...
      }
    };
  }


  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LOGD(TAG, "onDestroy");
  }



  @Override
  protected void onRestart() {
    super.onRestart();
    LOGD(TAG, "onRestart");
  }






  @Override
  protected int getNavigationItemID() {
    return R.id.action_run;
  }

  /**
   * view 초기화 설정
   */
  public void initView() {
    mRun = findViewById(R.id.btn_run);
  }
  public void seListeners(){
    mRun.setOnClickListener(this);
  }


  @Override
  public void onClick(View view) {
    //Todo: 버튼 visibility
    mRun.setVisibility(view.GONE);
    //Todo: RunningActivity의 역할을 MainActiviy에서, fragmentTransition으로 바꾸기
    RunningFragment runningFragment = new RunningFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.homeContainer,runningFragment,"RUNNINGFRAGMENT")
            .addToBackStack(null)
            .commit();
  }
}

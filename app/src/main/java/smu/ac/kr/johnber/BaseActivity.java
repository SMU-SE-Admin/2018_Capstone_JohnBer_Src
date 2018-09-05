package smu.ac.kr.johnber;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import smu.ac.kr.johnber.account.loginActivity;
import smu.ac.kr.johnber.course.CourseActivity;
import smu.ac.kr.johnber.my.MyActivity;
import smu.ac.kr.johnber.run.MainActivity;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * Created by yj34 on 26/03/2018.
 * Activity들은 BaseActivity를 상속받아 구현
 * 기본적인 공통기능 수행
 * - Toolbar
 * - Toolbar title
 * - Bottom NavigationBar
 */

public class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

  private Toolbar mToolbar;
  private BottomNavigationView mBottomNavigation;
  private TextView mToolbarTitle;

  private final static String TAG = makeLogTag(BaseActivity.class);


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);



  }



  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    setToolbar();
    setBottomNavigation();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        //로그인화면으로 돌아가기
        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(intent);

        return true;

        default:
          return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.action_course:
        intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        finish();
        break;
      case R.id.action_run:
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        break;
      case R.id.action_statistics:
        intent = new Intent(this, MyActivity.class);
        startActivity(intent);
        finish();
        break;
    }



    return false;
  }

  protected void setToolbar() {
    if (mToolbar == null) {
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
      if (mToolbar != null) {
        setSupportActionBar(mToolbar);
        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbarTitle);
        if (mToolbarTitle != null) {
          int titleId = getNavigationItemID();
          if (titleId != 0) {
            switch (titleId) {
              case R.id.action_course:
                mToolbarTitle.setText(R.string.navigation_course);

                break;
              case R.id.action_run:
                mToolbarTitle.setText(R.string.navigation_run);
                break;
              case R.id.action_statistics:
                mToolbarTitle.setText(R.string.navigation_statistics);
                break;
            }

          }
        }
        //기본 toolbar title 숨김. custom toolbar 사용
        getSupportActionBar().setDisplayShowTitleEnabled(false);
      }
    }
  }

  protected void setBottomNavigation() {
    if (mBottomNavigation == null) {
      mBottomNavigation = findViewById(R.id.bottomNavigationView);
      if (mBottomNavigation != null) {
        setNavigationItemClicked();
      }
      /**
       * set bottomNav icon size
       */
      mBottomNavigation.setOnNavigationItemSelectedListener(this);
      BottomNavigationMenuView menuView = (BottomNavigationMenuView) mBottomNavigation.getChildAt(0);
      for (int i = 0; i < menuView.getChildCount(); i++) {
        final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
        final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);
        iconView.setLayoutParams(layoutParams);
      }
    }
  }

  /**
   * @return 현재 화면 이름
   */
  protected int getNavigationItemID() {
    return 0;
  }


  protected void setNavigationItemClicked() {
    int navId = getNavigationItemID();
    switch (navId) {
      case R.id.action_course:
        mBottomNavigation.setSelectedItemId(R.id.action_course);
        break;
      case R.id.action_run:
        mBottomNavigation.setSelectedItemId(R.id.action_run);
        break;
      case R.id.action_statistics:
        mBottomNavigation.setSelectedItemId(R.id.action_statistics);
        break;
    }
  }


}
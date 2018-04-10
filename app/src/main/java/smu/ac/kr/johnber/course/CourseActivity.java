package smu.ac.kr.johnber.course;

import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;

public class CourseActivity extends BaseActivity {

  private final static String TAG = makeLogTag(CourseActivity.class);

//Todo: 레이아웃이 정상적으로 보이려면 viewHolder, Adapter...구현해야함
  //Todo : content fragment xml 파일 수정. dynamic 방식으로 추가할것
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.course_act);
  }


  @Override
  protected int getNavigationItemID() {
    return R.id.action_course;
  }
}

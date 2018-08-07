package smu.ac.kr.johnber.my;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.course.CourseDetailFragment;
import smu.ac.kr.johnber.map.JBLocation;
import smu.ac.kr.johnber.run.Record;

public class MyActivity extends BaseActivity implements MyCourseViewHolder.itemClickListener {
  private final static String TAG = makeLogTag(MyActivity.class);
  private TextView courseName;
  public TextView startPoint;
  public TextView distance;
  public TextView calories;
  public TextView time;
  private int dataNO;
  private View mView;
  private Marker mMarker;
  private MapView mMapView;
  private GoogleMap mgoogleMap;
  private Realm mRealm;
  private MyCourseViewHolder.itemClickListener listener;
  private List<Record> mockRecords ;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.my_act);

    MyStatisticsPagerAdapter myStatisticsPagerAdapter = new MyStatisticsPagerAdapter();
    ViewPager myStatisticsviewPager = findViewById(R.id.my_statistics_viewPager);
    myStatisticsviewPager.setAdapter(myStatisticsPagerAdapter);

    //        set on page listener 구현?
    //TODO : 리사이클러뷰 어댑터에 데이터 넘겨줌(Record)
    mockRecords = generateMockRecords();
    MyCourseAdapter adapter = new MyCourseAdapter(this, mockRecords, this);
    RecyclerView recyclerView = findViewById(R.id.my_rv_course);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);


  }

  @Override
  protected int getNavigationItemID() {
    return R.id.action_statistics;
  }

  //TODO :  여기서 test 용 reocord 객체 생성 !!!!!!!!!!!!!!!!!!!
  private List<Record> generateMockRecords() {

    List<Record> mock = new ArrayList<>();

    SharedPreferences preferences;
    preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);
    Gson gson = new Gson();
    String response = preferences.getString("LOCATIONLIST", "");
    ArrayList<JBLocation> locationArrayList = gson.fromJson(response, new TypeToken<List<JBLocation>>(){}.getType());

    double distance = Double.parseDouble(preferences.getString("DISTANCE", "0"));
    double elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", "0"));
    double calories = Double.parseDouble(preferences.getString("CALORIES", "0"));
    double startTime = Double.parseDouble(preferences.getString("STARTTIME", "0"));
    double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));
    String sDate = preferences.getString("DATE", "0");
    try {
      Date date = new SimpleDateFormat("MM/dd/yyy").parse(sDate);

    String title = sDate + " RUN";   // date를 변환해서 우선 넣기로함


    for(int i =0; i<10; i++){
      mock.add(new Record(distance, elapsedTime, calories, locationArrayList, date, startTime, endTime, title));
    }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return mock;

  }

  public List<Record> getMockRecords(){
    return this.mockRecords;
  }
  @Override
  public void onItemClicked(View view, int position) {
    LOGD(TAG,"CLICKED!"+position);
    showDeatilView(position,view);
  }

  public void showDeatilView(int position, View view) {
    Bundle data = new Bundle();
    data.putInt("position", position);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    MyCourseDetailFragment fragment = new MyCourseDetailFragment();
    fragment.setArguments(data);
    fragmentTransaction.add(R.id.mycourse_item_container,fragment,"MYCOURSEDETAILFRAGMENT")
            .addToBackStack(null)
            .commit();
  }

}

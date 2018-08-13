package smu.ac.kr.johnber.my;

import static android.widget.LinearLayout.VERTICAL;
import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    //  private List<Record> mockRecords ;
    private List<Record> recordItems;
    private MyCourseAdapter adapter;

    private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_act);

        MyStatisticsPagerAdapter myStatisticsPagerAdapter = new MyStatisticsPagerAdapter();
        ViewPager myStatisticsviewPager = findViewById(R.id.my_statistics_viewPager);
        myStatisticsviewPager.setAdapter(myStatisticsPagerAdapter);

        //        set on page listener 구현?
//    if (mockRecords != null) {
//      mockRecords.clear();
//    }

        if (recordItems == null) {
            recordItems = new ArrayList<>();
            this.getRecord();
        }
//    mockRecords = generateMockRecords();
        adapter = new MyCourseAdapter(this, recordItems, this);
        RecyclerView recyclerView = findViewById(R.id.my_rv_course);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
//            recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
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
        ArrayList<JBLocation> locationArrayList = gson.fromJson(response, new TypeToken<List<JBLocation>>() {
        }.getType());

        double distance = Double.parseDouble(preferences.getString("DISTANCE", "0"));
        double elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", "0"));
        double calories = Double.parseDouble(preferences.getString("CALORIES", "0"));
        double startTime = Double.parseDouble(preferences.getString("STARTTIME", "0"));
        double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));
        String sDate = preferences.getString("DATE", "0");
        try {
            Date date = new SimpleDateFormat("MM/dd/yyy").parse(sDate);

            String title = sDate + " RUN";   // date를 변환해서 우선 넣기로함


            for (int i = 0; i < 10; i++) {
                mock.add(new Record(distance, elapsedTime, calories, locationArrayList, date, startTime, endTime, title));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mock;

    }

    public void getRecord() {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();

                //DB에 저장된 데이터 HashMap에 저장.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //DB에서 로그인한 아이디와 일치하는지 확인 후 해당 데이터만 읽어옴.
                    if (snapshot.getKey().toString().equals(uid)) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        String keyDate1 = "";
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            keyDate1 = snapshot1.getKey().toString();
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                String keyDate = keyDate1 + '/' + snapshot2.getKey().toString();
                                recordHashMap.put(keyDate, snapshot2.getValue(Record.class));
                                recordItems.add(snapshot2.getValue(Record.class));
//             LOGD(TAG,"HASHMAPSIZE0 : "+recordHashMap.size());
//             LOGD(TAG,"record-location : "+ recordHashMap.get(keyDate).getJBLocation().get(0).getmLatitude());
                            }
                        }
                    }
//         recordItems = (List<Record>) recordHashMap.values();

//          LOGD(TAG,"Size : "+recordItems.size();
                    if (adapter != null) {

                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Record> getRecordsItems() {
        return this.recordItems;
    }

    @Override
    public void onItemClicked(View view, int position) {
        LOGD(TAG, "CLICKED!" + position);
        showDeatilView(position, view);
    }


    public void showDeatilView(int position, View view) {
        Bundle data = new Bundle();
        data.putInt("position", position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MyCourseDetailFragment fragment = new MyCourseDetailFragment();
        fragment.setArguments(data);
        fragmentTransaction.add(R.id.mycourse_item_container, fragment, "MYCOURSEDETAILFRAGMENT")
                .addToBackStack(null)
                .commit();
    }

}

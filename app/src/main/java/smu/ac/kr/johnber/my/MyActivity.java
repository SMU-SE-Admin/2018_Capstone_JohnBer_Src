package smu.ac.kr.johnber.my;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.BitmapUtil;

public class MyActivity extends BaseActivity implements MyCourseViewHolder.itemClickListener {
    private final static String TAG = makeLogTag(MyActivity.class);
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private TextView userName;
    public TextView distance;
    public TextView calories;
    public TextView time;
    public ImageView profileView;
    private FirebaseAuth mAuth;
    private List<Record> recordItems;
    private MyCourseAdapter adapter;
    private SharedPreferences pref ;
    private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_act);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userName = findViewById(R.id.tv_my_username);
        userName.setText(user.getDisplayName());
            pref = getSharedPreferences("pref", MODE_PRIVATE);
        //TODO : 프로필 사진
        profileView = findViewById(R.id.imageView3);

        if (pref.getString("profileImage",null) != null) {
            Glide.with(this)
                    .load(BitmapUtil.decodeBase64(pref.getString("profileImage", null)))
                    .apply(new RequestOptions().circleCrop()).into(profileView)
            ;
        }
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프로필사진 찍기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        if (recordItems == null) {
            recordItems = new ArrayList<>();
            this.getRecord();
        }

        adapter = new MyCourseAdapter(this, recordItems, this);
        RecyclerView recyclerView = findViewById(R.id.my_rv_course);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        MyStatisticsPagerAdapter myStatisticsPagerAdapter = new MyStatisticsPagerAdapter(this,recordHashMap);
        ViewPager myStatisticsviewPager = (ViewPager)findViewById(R.id.my_statistics_viewPager);
        myStatisticsviewPager.setAdapter(myStatisticsPagerAdapter);

        /**
         * skeleton 로딩 구현
         * option1 : recyclerview - setAdapter 주석처리할것
         * option2 : View
         */
        recyclerView.setAdapter(adapter);
        View rootView = findViewById(R.id.mycourse_item_container);
        skeletonScreen = Skeleton.bind(rootView)
                .shimmer(true)
                .duration(2000)
                .color(R.color.shimmer_color)
                .load(R.layout.activity_view_skeleton)
                .show();
        MyHandler myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(1, 1000);
    }

    //사진 결과 받아와서 sharedPreference에 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //sharedPreference에 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("profileImage", BitmapUtil.encodeTobase64(imageBitmap));
            editor.commit();

            Glide.with(this)
                    .load(imageBitmap)
                    .apply(new RequestOptions().circleCrop()).into(profileView)
            ;
        }
    }

    @Override
    protected int getNavigationItemID() {
        return R.id.action_statistics;
    }

    public void getRecord() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();

                Iterable<DataSnapshot> ds = dataSnapshot.child(uid).child("userRecord").getChildren();
                //DB에 저장된 데이터 HashMap에 저장.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //DB에서 로그인한 아이디와 일치하는지 확인 후 해당 데이터만 읽어옴.
                    if (snapshot.getKey().toString().equals(uid)) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        String keyDate1 = "";
                        for (DataSnapshot snapshot1 : snapshot.child("userRecord").getChildren()) {
                            keyDate1 = snapshot1.getKey().toString();
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                String keyDate = keyDate1 + '/' + snapshot2.getKey().toString();
                                recordHashMap.put(keyDate, snapshot2.getValue(Record.class));
                                recordItems.add(snapshot2.getValue(Record.class));
                            }
                        }
                    }
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

    //for skeleton view
    public static class MyHandler extends android.os.Handler {
        private final WeakReference<MyActivity> activityWeakReference;

        MyHandler(MyActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();
            }
        }
    }
}
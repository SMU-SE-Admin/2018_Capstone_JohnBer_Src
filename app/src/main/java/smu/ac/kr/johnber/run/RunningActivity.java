package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.account.UserProfile;
import smu.ac.kr.johnber.map.JBLocation;

/**
 * A simple {@link Fragment} subclass.
 */
//Todo: PauseRunningFragment에서 return누르는경우... Interface를 구현하여, 이벤트 발생시 운동기록을 db에 저장하고(백그라운드서비스) MainActivity로 전환할것

/**
 * - 달리기 종료 후 데이터 저장
 * - fragment간 통신 구현
 */

public class RunningActivity extends AppCompatActivity implements PauseRunningFragment.RunFragCallBackListener {

    private final static String TAG = makeLogTag(RunningActivity.class);
    private Record mRecord;
    private Fragment runningFragment;
    private Bitmap bitmap;
    //UserProfile profile = ((UserProfile)getApplicationContext());



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_running_activity);
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onStart() {
        super.onStart();
        LOGD(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LOGD(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LOGD(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LOGD(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LOGD(TAG, "onDestroy");
    }

    public void initView() {
        // 달리기 fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.run_running_status_container, new RunningFragment(), "RUNNINGFRAGMENT")
                .addToBackStack(null)
                .commit();

    }


    public void setRecord(Record record) {
        mRecord = record;
    }

    /**
     * Resume 버튼을 눌렀을때 동작
     * Resume버튼을 눌렀을 때 state를 Resume으로 바꾸고, runningFragment에서 start trackerService를 한다.
     */
    @Override
    public void onClickedResume() {
        LOGD(TAG, "onClikedReusme");
        FragmentManager fragmentManager = getSupportFragmentManager();
        RunningFragment runfrag = (RunningFragment) fragmentManager.findFragmentByTag("RUNNINGFRAGMENT");
        runfrag.setState(20003);
        runfrag.resumebindService();
//   runfrag.resumestartTimer();
    }

    /**
     * sharedPreference에 있는 데이터 + date, endTime, Title -> Record객체에 저장
     * firebase에 저장
     */


    @Override
    public void onClickedReturn() {
        LOGD(TAG, "onClikedReturn ~ save data");

        /**
         * 지도 screen capture
         */
        SharedPreferences preferences;
        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = preferences.getString("LOCATIONLIST", "");
        ArrayList<JBLocation> locationArrayList = gson.fromJson(response, new TypeToken<List<JBLocation>>() {
        }.getType());

        // 나머지 복원
        double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));
        double distance = Double.parseDouble(preferences.getString("DISTANCE", "0"));
        double elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", "0"));
        double calories = Double.parseDouble(preferences.getString("CALORIES", "0"));
        double startTime = Double.parseDouble(preferences.getString("STARTTIME", "0"));
//    double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));


//    Date date = null;

        LOGD(TAG, "!!!!" + preferences.getString("CALORIES", "0") +
                "\n" + preferences.getString("DISTANCE", "0") +
                "\n" + preferences.getString("ELAPSEDTIME", "0") +
                "\n" + preferences.getString("ENDTIME", "0") +
                "\n" + preferences.getString("STARTTIME", "0"));


        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        double kg = pref.getFloat("userWeight", 0);
        //if 14:50 -> 14.83333
        double seconds = (elapsedTime / 1000) % 60 *1/60;
        double min = elapsedTime/(1000*60) + seconds;
        calories = 7 * (3.5 * kg * min) * 5/1000;

        Log.d("Mainactivity", "*****calculateCalories : " + calories + ", " +kg);


        mRecord = new Record(distance, elapsedTime, calories, locationArrayList, null, startTime, endTime, null);
        /**
         * imgUrl : firebase storage에 저장된 img 주소
         */
//        String imgBaseUrl = uid + "/" + getTime + "/" + stringStartTime +"/";  // 수정 !!!!!!!!!!!!!!!!!!!!
        captureScreen();

    }

    private void captureScreen() {

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                String fileName = System.currentTimeMillis()+".jpg";
                bitmap = snapshot;
                FileOutputStream os = null;
                File imgFile = new File(getFilesDir(), fileName);
                LOGD(TAG, "file path : " + getFilesDir() + "/" + fileName);
                if (!imgFile.exists()) {
                    try {
                        imgFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    os = new FileOutputStream (imgFile);

                    //Write
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
                    os.flush();
                    os.close();
                } catch (FileNotFoundException e) {
                    LOGD(TAG, "ImageCapture : FileNotFoundException");
                    e.printStackTrace();
                } catch (IOException e) {
                    LOGD(TAG, "ImageCapture + IOException");
                    e.printStackTrace();
                }

                uploadImgtoFirebaseStorage(fileName);
            }
        };
        RunningFragment fg = (RunningFragment) getSupportFragmentManager().findFragmentByTag("RUNNINGFRAGMENT");
        GoogleMap map = fg.getGooglemap();
        map.snapshot(callback);
    }

    private void uploadImgtoFirebaseStorage(String fileName) {
        /**
         * firebase cloud
         */
        //현재 시간 받아오기.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //date format은 0000-00-00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String getTime = sdf.format(date);
        //nodeId로 사용할 startTime변환.
        SimpleDateFormat t_sdf = new SimpleDateFormat("HH:mm:ss");
        final String stringStartTime = t_sdf.format(now);
        //TODO : 코드 정리하기
        final String title = getTime + "/" + stringStartTime;   // date를 변환해서 우선 넣기로함
        //firebase.auth를 이용한 user id가져오기.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        String imgBaseUrl = uid + "/" + getTime + "/" + stringStartTime +"/";

        /**
         * firebase Storage
         */
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://capstonejohnbersrc.appspot.com");
        StorageReference storageReference = storage.getReference();
        String fileNameSpace = getFilesDir()+"/"+fileName;
        //파일 업로드
        File file = new File(fileNameSpace);
            Uri fileUri = Uri.fromFile(file);
            //firebase storage에 참조 공간 만들기
        final StorageReference spaceReference = storageReference.child(imgBaseUrl+fileName);
        if(!fileName.equals("")){
            //파일 업로드
            UploadTask uploadTask = spaceReference.putFile(fileUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //파일 업로드 실패에 대한 콜백 핸들링
                    LOGD(TAG,"failed to upload img");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //성공
                    LOGD(TAG, "uploading img to FB successed ");

                }
            });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // 이미지 URL 받아오기
                    return spaceReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                    //참조 uri 가저오기
                    if (mRecord != null) {
                        mRecord.setImgUrl(task.getResult().toString());
                        LOGD(TAG, "Downlodable uri : " + mRecord.getImgUrl());
                        mRecord.setDate(date);
                        mRecord.setTitle(title);
                        uploadRecordtoFirebase(mRecord,uid,getTime,stringStartTime);
                    }
                    } else { //에러처리
                    }
                }
            });

        }

    }

    private void uploadRecordtoFirebase(Record mRecord, String uid, String getTime, String stringStartTime) {
        // Write data to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child(uid).child("userRecord").child(getTime).child(stringStartTime).setValue(mRecord);
    }

}

package smu.ac.kr.johnber.opendata;

import android.content.Context;
import android.icu.util.Calendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourseResponse;
import smu.ac.kr.johnber.opendata.APImodel.WeatherResponse;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class CourseRequest {
    private static String TAG = makeLogTag(CourseRequest.class);

    private String startPoint;
    private String endPoint;
    private String course;
    private String courseInfo;
    private String courseName;
    private double time;
    private double distance;
    private String endpointAddr;
    private String startPointAddr;

    private static final int pageNo = 1;
    private static final int listNo = 10;
    private static final String responseType = "json";

    private Realm mRealm;
    private JSONObject jsonObject;
    private Context context;

    public CourseRequest(Context context) {
        this.context = context;
    }

    public void loadCourseData(){
        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_RUNNINGCOURSE)
                        .getApiService();

        final Call<ResponseBody> reponse =
                apiService.loadRunningCourse(ApiService.DATAGOKR_API_SERVICE_KEY,pageNo,listNo,responseType);

        reponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LOGD(TAG, "server contacted at: " + call.request().url());
                Realm.init(context);
                mRealm = Realm.getDefaultInstance();

                try {
                    //response body를 string으로 변환
                    String json = response.body().string();
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i< jsonArray.length();i++){
                        //Array안의 각 코스 Ojbect를 꺼낸다.
                        jsonObject = jsonArray.getJSONObject(i);
                        insertCourseData();
                    }
                    LOGD(TAG, "category: "+jsonArray.length()
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LOGD(TAG, "inserted : " + getCourseListSize());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LOGD(TAG, "server failed at: " );
            }
        });
    }

    private void insertCourseData() {
        mRealm.beginTransaction();
        RunningCourseResponse runningCourseResponse = mRealm.createObject(RunningCourseResponse.class);
        //RunningCourseResponse 객체에 매핑
        try {
            runningCourseResponse.setEndPoint(jsonObject.get("종료지점명").toString());
            runningCourseResponse.setStartPoint(jsonObject.get("시작지점명").toString());
            runningCourseResponse.setCourse(jsonObject.get("경로정보").toString());
            runningCourseResponse.setCourseInfo(jsonObject.get("길소개").toString());
            runningCourseResponse.setCourseName(jsonObject.get("길명").toString());
            // TOdo: 시간, 길이 데이터 포맷이 모두 달라 정규화가 필요함 + null 인경우가 더 많음 -> 직접 계산할것
            runningCourseResponse.setTime(3000);
            runningCourseResponse.setDistance(1000);
            runningCourseResponse.setEndpointAddr(jsonObject.get("종료지점소재지지번주소").toString());
            runningCourseResponse.setStartPointAddr(jsonObject.get("json시작지점소재지지번주소").toString());
//                        LOGD(TAG,"object : "+jsonObject.toString());
        } catch (JSONException e) {}
        mRealm.commitTransaction();
    }

    private int getCourseListSize(){
        RealmResults<RunningCourseResponse> list = mRealm.where(RunningCourseResponse.class).findAll();
        return list.size();
    }
}

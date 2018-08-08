package smu.ac.kr.johnber.opendata;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class CourseRequest {
    private static String TAG = makeLogTag(CourseRequest.class);


    private static final int pageNo = 1;
    private static final int listNo = 500;
    private static final int listNo = 800;
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
                        //TODO:
                        if(!jsonObject.get("총길이").toString().equals("null")
                                &&!jsonObject.get("총소요시간").toString().equals("null")
                                &&jsonObject.get("길소개").toString().length()>50){
                        insertCourseData();}
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
        RunningCourse runningCourse = mRealm.createObject(RunningCourse.class);
        //RunningCourse 객체에 매핑
        try {
            runningCourse.setEndPoint(jsonObject.get("종료지점명").toString());
            runningCourse.setStartPoint(jsonObject.get("시작지점명").toString());
            runningCourse.setCourse(jsonObject.get("경로정보").toString());
            runningCourse.setCourseInfo(jsonObject.get("길소개").toString());
            runningCourse.setDistance(jsonObject.get("총길이").toString());
            runningCourse.setCourseName(jsonObject.get("길명").toString());
            runningCourse.setTime(jsonObject.get("총소요시간").toString());
            runningCourse.setStartPointAddr(jsonObject.get("시작지점소재지지번주소").toString());
            runningCourse.setStartPointRoadAddr(jsonObject.get("시작지점도로명주소").toString());
            runningCourse.setEndPointAddr(jsonObject.get("종료지점소재지지번주소").toString());
            runningCourse.setEndPointRoadAddr(jsonObject.get("종료지점소재지도로명주소").toString());

        } catch (JSONException e) {}
        mRealm.commitTransaction();
    }

    private int getCourseListSize(){
        RealmResults<RunningCourse> list = mRealm.where(RunningCourse.class).findAll();
        return list.size();
    }
}

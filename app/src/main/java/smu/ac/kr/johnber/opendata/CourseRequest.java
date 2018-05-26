package smu.ac.kr.johnber.opendata;

import android.content.Context;
import android.icu.util.Calendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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


    public static void loadCourseData(Context context){
        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_RUNNINGCOURSE)
                        .getApiService();

        final Call<ResponseBody> reponse =
                apiService.loadRunningCourse(ApiService.DATAGOKR_API_SERVICE_KEY,pageNo,listNo,responseType);

        reponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LOGD(TAG, "server contacted at: " + call.request().url());
                LOGD(TAG, "category: "+response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LOGD(TAG, "server failed at: " );
            }
        });
    }
}

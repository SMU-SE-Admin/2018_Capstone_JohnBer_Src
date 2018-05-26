package smu.ac.kr.johnber.opendata.network;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourseResponse;
import smu.ac.kr.johnber.opendata.APImodel.WeatherResponse;


/**
 * Created by yj34 on 21/03/2018.
 */

public interface ApiService {

    public static final String DATAGOKR_API_SERVICE_KEY = "VEQG7Dkn9phMbx%2Bvwni9uI8YB0Wk37FPh%2BFaIhIqXo3dh%2FXvJdkOhSs6XXHpHdfvL25UEU%2FLriN4idb8l7bUIw%3D%3D";
    //  동네 예보
    @GET("ForecastGrib")
    Call<WeatherResponse> loadWeatherForecast(@Query(value = "ServiceKey", encoded = true) String serviceKey,
                                              @Query("base_date") String baseDate, @Query("base_time") String baseTime,
                                              @Query("nx") int nx, @Query("ny") int ny, @Query("pageNo") int pageNo, @Query("numOfRows") int numOfRows, @Query("_type") String responseType);


    // 길 정보
    @GET("stret-tursm-info-std")
    Call<ResponseBody> loadRunningCourse(@Query(value = "serviceKey", encoded = true) String serviceKey,
                                         @Query("s_page") int pageNo, @Query("s_list") int listNo, @Query("type") String responseType);
}

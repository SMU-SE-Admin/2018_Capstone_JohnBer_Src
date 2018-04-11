package smu.ac.kr.johnber.opendata.network;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import smu.ac.kr.johnber.opendata.weatherforecastmodel.WeatherResponse;


/**
 * Created by yj34 on 21/03/2018.
 */

public interface ApiService {

  public static final String DATAGOKR_API_SERVICE_KEY = "VEQG7Dkn9phMbx%2Bvwni9uI8YB0Wk37FPh%2BFaIhIqXo3dh%2FXvJdkOhSs6XXHpHdfvL25UEU%2FLriN4idb8l7bUIw%3D%3D";
//  Call<List<WeatherForecast>>
  @GET("ForecastGrib")
  Call<WeatherResponse>loadWeatherForecast(@Query(value = "ServiceKey", encoded = true) String serviceKey,
  @Query("base_date") String baseDate, @Query("base_time") String baseTime,
      @Query("nx") int nx,@Query("ny") int ny,@Query("pageNo") int pageNo,@Query("numOfRows") int numOfRows, @Query("_type") String responseType);

}

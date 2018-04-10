package smu.ac.kr.johnber.opendata;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;

import android.icu.util.Calendar;
import java.text.SimpleDateFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;
import smu.ac.kr.johnber.opendata.weatherforecastmodel.WeatherResponse;

/**
 * Created by yj34 on 25/03/2018.
 * 동네예보 미세먼지 api
 */

public class WeatherForecast {
  private static String TAG = makeLogTag(WeatherForecast.class);



  private static String baseDate = "20180331";
  private static String baseTime = "0800";
  private static int nx = 24;
  private static int ny = 64;
  private static final int pageNo = 1;
  private static final int numOfRows = 10;
  private static final String responseType = "json";
  public static WeatherForecast weatherForecast;
  private WeatherForecast(String classNmae) {
    TAG = classNmae;
    weatherForecast = new WeatherForecast(classNmae);
  }

  public static void setBaseDate() {
    Calendar calendar = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      calendar = Calendar.getInstance();
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyMMDD-hh:mm:ss aa");
    String datetime=null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      datetime = dateFormat.format(calendar.getTime());
    }
    String[] splitdateTime = datetime.split("/");
    String baseDate = splitdateTime[0];
    WeatherForecast.baseDate = baseDate;
  }

  public static void setTime(String baseTime) {
    WeatherForecast.baseTime = baseTime;
  }

  public static void setLoation(int nx, int ny) {
    WeatherForecast.nx = nx;
    WeatherForecast.ny = ny;
  }



  public static void loadWeatherData(Context context){
    setBaseDate();
    //Todo : 레이아웃 처리
    /**
     *items에 List<item> ~ item각 순서대로 코드를 포함고있음. 코드~ 날씨정보 정규화필요 ~ 클래스로 따로 뺴기
     */
    ApiService apiService =
        ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_WEATHERFORECAST)
            .getApiService();

    final Call<WeatherResponse> weatherDataReponse =
        apiService.loadWeatherForecast(ApiService.DATAGOKR_API_SERVICE_KEY,
            baseDate,
            baseTime,
            nx,ny,
            pageNo,
            numOfRows,
            responseType);

    weatherDataReponse.enqueue(new Callback<WeatherResponse>() {
      @Override
      public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
        LOGD(TAG, "server contacted at: " + call.request().url());
        WeatherResponse weatherResponse = response.body();
        //카테고리 가져오기
        String category = weatherResponse.getResponse().getBody().getItems().getItem().get(0).getCategory();
        LOGD(TAG, "category: "+category);
      }

      @Override
      public void onFailure(Call<WeatherResponse> call, Throwable t) {
        LOGD(TAG, "server failed at: " );
      }
    });
  }





}

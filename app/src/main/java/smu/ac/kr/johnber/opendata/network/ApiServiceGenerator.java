package smu.ac.kr.johnber.opendata.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import smu.ac.kr.johnber.BuildConfig;
import smu.ac.kr.johnber.util.LogUtils;

/**
 * Created by yj34 on 21/03/2018.
 */

public class ApiServiceGenerator {

    private static final String TAG = LogUtils.makeLogTag(ApiServiceGenerator.class);

    //싱글톤 구현
    private static String BASE_URL = null;
    public static final int BASE_URL_TYPE_WEATHERFORECAST = 3301;
    public static final int BASE_URL_TYPE_DUSTCONENTRATION = 3302;
    public static final int BASE_URL_TYPE_RUNNINGCOURSE = 3303;
    public static final int BASE_URL_TYPE_PLACE = 3304;

    private static final String BASE_URL_PLACE = "https://maps.googleapis.com/maps/api/place/";
    private static final String BASE_URL_WEATHERFORECAST = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/";
    private static final String BASE_URL_DUSTCONENTRATION = "";
    private static final String BASE_URL_RUNNINGCOURSE = "http://api.data.go.kr/openapi/";
    private static final String BASE_URL_NAVER_GEO = "https://openapi.naver.com/v1/map/";
    private static ApiServiceGenerator apiServiceGenerator = new ApiServiceGenerator();
    private static ApiService apiService;

    private static GsonConverterFactory gsonFactory = GsonConverterFactory.create();

    private static HttpLoggingInterceptor loggingInterceptor =
            new HttpLoggingInterceptor()
                    .setLevel(Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Builder();

    //retrofit을 통해 ApiService객체를 만든다 , apiService.함수 호출 response call<t>로 받고 .eneque하여 성공실패로직 작성
    private ApiServiceGenerator() {

    }

    public static ApiServiceGenerator getApiServiceGenerator(int baseURLType) {

        switch (baseURLType) {
            case (BASE_URL_TYPE_WEATHERFORECAST):
                BASE_URL = BASE_URL_WEATHERFORECAST;
                break;
            case (BASE_URL_TYPE_DUSTCONENTRATION):
                BASE_URL = BASE_URL_DUSTCONENTRATION;
                break;
            case (BASE_URL_TYPE_RUNNINGCOURSE):
                BASE_URL = BASE_URL_RUNNINGCOURSE;
                break;
            case BASE_URL_TYPE_PLACE:
                BASE_URL = BASE_URL_PLACE;
                break;
            default:
                throw new UnsupportedOperationException(baseURLType + ":" + " / " + BASE_URL);
        }

        //디버그 모드일때만 로깅
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(loggingInterceptor);
        }

        builder.baseUrl(BASE_URL)
                .addConverterFactory(gsonFactory)
                .client(httpClient.build());
        return apiServiceGenerator;
    }

    public static ApiService getApiService() {
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(ApiService.class);
        return apiService;
    }


}

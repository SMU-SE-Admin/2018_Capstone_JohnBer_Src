package smu.ac.kr.johnber.opendata.network;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import smu.ac.kr.johnber.opendata.PlaceAPImodel.PlaceDetails;


/**
 * Created by yj34 on 21/03/2018.
 */

public interface ApiService {

    String DATAGOKR_API_SERVICE_KEY = "VEQG7Dkn9phMbx%2Bvwni9uI8YB0Wk37FPh%2BFaIhIqXo3dh%2FXvJdkOhSs6XXHpHdfvL25UEU%2FLriN4idb8l7bUIw%3D%3D";
    String GOOGLE_MAPS_API_SERVICE_KEY = "AIzaSyDhkhaiqWWQ4uoKGM8vgdHRAvE_RwIDo00";

    // 길 정보
    @GET("stret-tursm-info-std")
    Call<ResponseBody> loadRunningCourse(@Query(value = "serviceKey", encoded = true) String serviceKey,
                                         @Query("s_page") int pageNo, @Query("s_list") int listNo, @Query("type") String responseType);

    // Place Search
    // key, input(location name), inputtype=textquery)
    // language
    // fields=photos(for photo reference), place_id
    //locationbiase = point:lat,lang
    //key=
    //https://square.github.io/retrofit/2.x/retrofit/index.html?retrofit2/http/QueryMap.html
    @GET("findplacefromtext/json")
    Call<ResponseBody> callPlaceSearch(@Query(value = "input", encoded = true) String locationQuery,
                                       @Query("inputtype") String inputtype,
                                       @Query("fields") String fields,
                                       @Query("locationbiase") String locationbiase,
                                       @Query(value = "key", encoded = true) String serviceKey);


    //* place_id is required
    //place_id
    //fields=opening_hours,formatted_phone_number,website,review,rating
    @GET("details/json")
    Call<PlaceDetails> callPlaceDetails(@Query("placeid") String placeid,
                                        @Query("fields") String fields,
                                        @Query(value = "key", encoded = true) String serviceKey);


    //*) photoreference    / maxheihgt or maxwidth
    @GET("photo")
    Call<ResponseBody> callPlacePhotos(
            @Query("maxwidth") int maxwidth,
            @Query("photoreference") String photoref,
            @Query(value = "key", encoded = true) String serviceKey);

}

package smu.ac.kr.johnber.opendata;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.course.CourseViewHolder;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.network.ApiHelper;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class PlaceRequest {
    //for google maps api  - place search, details and photos
    // 장소 검색 - 구글 api를 이용해 해당 장소의 정보를 가져옴
    private static String TAG = makeLogTag(PlaceRequest.class);
    private static String place_id;
    //    private static String place_id;
    private GeoDataClient mGeoDataClient;
    private RunningCourse course;
    private CourseViewHolder holder;
    private int position;
    private Context context;

    private ApiService apiService;

    public PlaceRequest(Context context, RunningCourse courseItem) {
        this.course = courseItem;
        this.holder = holder;
        this.position = position;
        this.mGeoDataClient = mGeoDataClient;
        this.context = context;
        apiService = ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                .getApiService();
    }


    public void getStaticMapImg(RunningCourse course) {

        List<LatLng> list = getLatLangFromAddr(course);
        String staticURL = "https://maps.googleapis.com/maps/api/staticmap?zoom=14&size=200x200" +
                "&markers=color:blue%7Clabel:S%7C"
                +list.get(0).latitude+","+list.get(0).longitude
                +"&markers=color:yellow%7Clabel:E%7C"
                +list.get(1).latitude+","+list.get(1).longitude +"&key="
                +ApiService.GOOGLE_MAPS_API_SERVICE_KEY;
//        LOGD(TAG, "static url "+course.getCourseName()+": "+staticURL);
        Uri uri = Uri.parse(staticURL);
        if (uri != null) {
            //placeholder : 이미지 로딩중 미리 보여지는 이미지
            Glide.with(context)
                    .load(uri)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_point_marker)
                            .error(R.drawable.ic_dust_testicon_replacelater)
                    )
                    .thumbnail(0.1f)
                    .into(holder.thumnail);
        }
    }

    public void findPlaceID(final String locationName, LatLng latLng) {
        if (apiService == null) {
            apiService =
                    ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                            .getApiService();
        }

        final Call<ResponseBody> responseBodyCall =
                apiService.callPlaceSearch(locationName, "textquery",
                        ApiHelper.getQueryFeilds(ApiHelper.PLACE_SEARCH_FEILDS_REQUEST),
                        ApiHelper.getLocationbiase(latLng),
                        ApiService.GOOGLE_MAPS_API_SERVICE_KEY);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String placeID = null;
                String photoref = null;
//

                try {
                    //response body를 string으로 변환
                    String json = response.body().string();
                    JSONObject jsonObject = new JSONObject(json);
                    //candidates
                    //status
                    String status = jsonObject.getString("status");
//                    LOGD(TAG, "status : " + status);
                    if (status.equals("OK")) {
                        JSONObject candidates = jsonObject.getJSONArray("candidates").getJSONObject(0);

                        String formatted_address = candidates.getString("formatted_address");
                        placeID = candidates.getString("place_id");
                        if (candidates.has("photos")) {
                            photoref = candidates.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                        }

                            LOGD(TAG, "place address: " +course.getStartPoint()+":"+ formatted_address);
                            LOGD(TAG, "place id is  r: " +course.getStartPoint()+":"+ placeID);
                            LOGD(TAG, "photo reference: " +course.getStartPoint()+":"+ photoref);
                        //이미지 세팅
                        setPhototoThumnailView(placeID, photoref, course, holder, position);

                        //
                    } else {
//                        LOGD(TAG, "place request error code : " + status);
                        // static map 으로 마커표시해서 보여주기
                        getStaticMapImg(course);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LOGD(TAG, "failed to connected at " + call.request());
            }
        });

    }

//    public void findPlaceID(final String locationName, LatLng latLng) {
//        if (apiService == null) {
//            apiService =
//                    ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
//                            .getApiService();
//        }
//
//        final Call<ResponseBody> responseBodyCall =
//                apiService.callPlaceSearch(locationName, "textquery",
//                        ApiHelper.getQueryFeilds(ApiHelper.PLACE_SEARCH_FEILDS_REQUEST),
//                        ApiHelper.getLocationbiase(latLng),
//                        ApiService.GOOGLE_MAPS_API_SERVICE_KEY);
//        responseBodyCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                String placeID = null;
//                String photoref = null;
////                LOGD(TAG, "server contacted at: "+locationName + call.request().url());
//                try {
//                    //response body를 string으로 변환
//                    String json = response.body().string();
//                    JSONObject jsonObject = new JSONObject(json);
//                    //candidates
//                    //status
//                    String status = jsonObject.getString("status");
////                    LOGD(TAG, "status : " + status);
//                    if (status.equals("OK")) {
//                        JSONObject candidates = jsonObject.getJSONArray("candidates").getJSONObject(0);
//
//                        String formatted_address = candidates.getString("formatted_address");
//                        placeID = candidates.getString("place_id");
//                        if (candidates.has("photos")) {
//                            photoref = candidates.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
//                        }
//
//                        LOGD(TAG, "place address: " +course.getStartPoint()+":"+ formatted_address);
//                        LOGD(TAG, "place id is  r: " +course.getStartPoint()+":"+ placeID);
//                        LOGD(TAG, "photo reference: " +course.getStartPoint()+":"+ photoref);
//                        //이미지 세팅
//                        setPhototoThumnailView(placeID, photoref, course, holder, position);
//
//                        //
//                    } else {
////                        LOGD(TAG, "place request error code : " + status);
//                        // static map 으로 마커표시해서 보여주기
//                        getStaticMapImg(course);
//                        return;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                LOGD(TAG, "failed to connected at " + call.request());
//            }
//        });
//
//    }


    public void getPlaceDetail() {

    }


    private void setPhototoThumnailView(String placeID, String photoRef, RunningCourse courseItem, final CourseViewHolder holder, int position) {

        if (apiService == null) {
            apiService =
                    ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                            .getApiService();
        }

        if (placeID == null || photoRef == null)
            return;

        String baseURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
        String url = baseURL + photoRef + "&key=" + ApiService.GOOGLE_MAPS_API_SERVICE_KEY;
        Uri uri = Uri.parse(url);
        LOGD(TAG,courseItem.getCourseName()+": "+url);
        if (uri != null) {
            //placeholder : 이미지 로딩중 미리 보여지는 이미지
            Glide.with(context)
                    .load(uri)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_point_marker)
                            .error(R.drawable.ic_dust_testicon_replacelater)
                    )
                    .thumbnail(0.1f)
                    .into(holder.thumnail);
            //listener
        }


    }


//    public void findPlaceID(final String locationName, LatLng latLng){
//        ApiService apiService =
//                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
//                        .getApiService();
//
//        final Call<ResponseBody> responseBodyCall =
//                apiService.callPlaceSearch(locationName, "textquery",
//                        ApiHelper.getQueryFeilds(ApiHelper.PLACE_SEARCH_FEILDS_REQUEST),
//                        ApiHelper.getLocationbiase(latLng),
//                        ApiService.GOOGLE_MAPS_API_SERVICE_KEY);
//        responseBodyCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                String placeID;
//                LOGD(TAG, "server contacted at: "+locationName
//                        + call.request().url());
//                try{
//                    //response body를 string으로 변환
//                    String json = response.body().string();
//                    JSONObject jsonObject = new JSONObject(json);
//                    //candidates
//                    //status
//                    String status = jsonObject.getString("status");
//                    LOGD(TAG, "status : " + status);
//                    if (status.equals("OK")) {
//                        placeID = jsonObject.getJSONArray("candidates").getJSONObject(0).getString("place_id");
//                        LOGD(TAG, "place id is  r: " + placeID);
//                        //이미지 세팅
////                            setPhototoThumnailView(placeID,course,holder,position);
//
//                        //
//                    }else {
//                        LOGD(TAG, "place request error code : " + status);
//                        // 기본 이미지로 세팅
//                        return;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                LOGD(TAG,"failed to connected at " +call.request());
//            }
//        });
//
//    }



    private List<LatLng> getLatLangFromAddr(RunningCourse mcourseData) {
        List<LatLng> latlng = new ArrayList<>();
        if (mcourseData == null)
            LOGD(TAG, "mcourseData is empty");
        try {
            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
            Geocoder mGeoCoder = new Geocoder(context, Locale.KOREA);
            List<Address> startLocation = null;
            List<Address> endLocation = null;
            if (!(mcourseData.getStartPointAddr().equals("null") || mcourseData.getStartPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
            } else if (!(mcourseData.getStartPointRoadAddr().equals("null") || mcourseData.getStartPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
            }

            if (!(mcourseData.getEndPointAddr().equals("null") || mcourseData.getEndPointAddr().equals("해당 없음"))) {
                //지번주소
                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());
                if (endLocation.size() <= 0) {
                    endLocation = startLocation;
                }
            } else if (!(mcourseData.getEndPointRoadAddr().equals("null") || mcourseData.getEndPointRoadAddr().equals("해당 없음"))) {
                //도로명주소
                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
            }
            LatLng start = new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude());
            LatLng end = new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude());
            latlng.add(start);
            latlng.add(end);
            LOGD(TAG, "size of lsitsts : " + latlng.size());


        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "cannot find location info " + mcourseData.getCourseName() + " " + mcourseData.getStartPointAddr() + " " + mcourseData.getStartPointRoadAddr() + " " + mcourseData.getEndPointAddr() + " " + mcourseData.getEndPointRoadAddr());
        }

        return latlng;
    }
}




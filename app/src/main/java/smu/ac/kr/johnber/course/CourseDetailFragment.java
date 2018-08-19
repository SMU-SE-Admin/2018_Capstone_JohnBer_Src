package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.net.Uri;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;
import smu.ac.kr.johnber.opendata.PlaceAPImodel.PlaceDetails;
import smu.ac.kr.johnber.opendata.network.ApiHelper;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;
import smu.ac.kr.johnber.run.RunningActivity;
import smu.ac.kr.johnber.util.BitmapUtil;
import smu.ac.kr.johnber.util.PermissionUtil;


/**
 * A simple {@link Fragment} subclass.
 */

//Todo : MAPVIEW setting
public class CourseDetailFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private final static String TAG = makeLogTag(CourseDetailFragment.class);
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private GeoDataClient mGeoDataClient;
    private TextView courseName;
    public TextView startPoint;
    public TextView endPoint;
    public TextView distance;
    public TextView calories;
    public TextView time;
    public TextView course;
    public TextView courseInfo;
    public TextView avgRates;
    public TextView phone;
    public ImageView btPhone;
    public ImageView btWebsite;
    public TextView website;
    private Realm mRealm;
    private String place_id;
    private int dataNO;
    private RunningCourse mcourseData;
    private View mView;
    private Marker mMarker;
    private MapView mMapView;
    private GoogleMap mgoogleMap;
    private ArrayList<LatLng> markerList;
    private Button mRun;
    private ScrollView scrollView;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlaceDetails placeDetails;
    private CoursePlaceInfoAdapter adapter;
    private  RecyclerView recyclerView;
    private detailFragListener callbacklistener;


    public CourseDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGD(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.course_detail_fragment, container, false);
        Realm.init(getActivity().getApplicationContext());
        mRealm = Realm.getDefaultInstance();
        dataNO = getArguments().getInt("position");
        courseName = mView.findViewById(R.id.tv_course_detail_summary_title);
        startPoint = mView.findViewById(R.id.tv_course_detail_summary_start_point);
        endPoint = mView.findViewById(R.id.tv_course_detail_summary_end_point);
        distance = mView.findViewById(R.id.tv_course_distance);
        calories = mView.findViewById(R.id.tv_course_calories);
        btWebsite = mView.findViewById(R.id.ic_website);
        btPhone = mView.findViewById(R.id.ic_call);
        mRun = mView.findViewById(R.id.btn_course_run);  //TODO !!!!!달리기 실행으로 연결
        mRun.setOnClickListener(this);
        scrollView = mView.findViewById(R.id.scrollview);

        time = mView.findViewById(R.id.tv_course_time);
        course = mView.findViewById(R.id.tv_course_detail_info);
        courseInfo = mView.findViewById(R.id.tv_course_detail_info_content);
        mMapView = this.mView.findViewById(R.id.course_map_view);
        mcourseData = mRealm.where(RunningCourse.class).findAll().get(dataNO);
        courseName.setText(mcourseData.getCourseName());
        course.setText(mcourseData.getCourse());
        courseInfo.setText(mcourseData.getCourseInfo());
        startPoint.setText(mcourseData.getStartPoint());
        endPoint.setText(mcourseData.getEndPoint());
        time.setText(mcourseData.getTime());
        distance.setText(mcourseData.getDistance());

        avgRates = mView.findViewById(R.id.tv_course_detail_info_AVGratings_additional);
        phone = mView.findViewById(R.id.tv_course_detail_info_numbers_additional);
        website = mView.findViewById(R.id.tv_course_detail_info_website_additional);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);





        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    mRun.setVisibility(View.GONE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            0,
                            getActivity().getWindow().getDecorView().getHeight());
                    animate.setDuration(300);
                    mRun.startAnimation(animate);


                } else {
                    mRun.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            getActivity().getWindow().getDecorView().getHeight(),
                            0);
                    animate.setDuration(300);
                    mRun.startAnimation(animate);
                }

            }
        });


        return mView;
    }


    public void setListener(detailFragListener listener) {
        this.callbacklistener = listener;
    }
    private void getPlaceID(final RunningCourse course) {
        //get place id from google place search
       place_id = null;
        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                        .getApiService();
        if (markerList!=null) {

            final Call<ResponseBody> responseBodyCall =
                    apiService.callPlaceSearch(course.getCourseName(), "textquery",
                            ApiHelper.getQueryFeilds(ApiHelper.PLACE_SEARCH_FEILDS_REQUEST),
                            ApiHelper.getLocationbiase(markerList.get(0)),
                            ApiService.GOOGLE_MAPS_API_SERVICE_KEY);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    String photoref = null;
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
                            place_id = candidates.getString("place_id");
                            if (candidates.has("photos")) {
//                                photoref = candidates.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                            }
//                            LOGD(TAG, "place address: " +course.getStartPoint()+":"+ formatted_address);
                            LOGD(TAG, "place id is  r: " +course.getStartPoint()+" : "+ place_id);
//                            LOGD(TAG, "photo reference: " +course.getStartPoint()+":"+ photoref);
                            //
                            if (place_id != null) {
                                getPlaceDetails(place_id);
                            }
                        } else {
//                        LOGD(TAG, "place request error code : " + status);
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        mMapView.onDestroy();
        callbacklistener.onBackPressed();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mgoogleMap = googleMap;


//        try {
//            //시작지점명 , 종료지점명으로부터 위도,경도 정보 알아내기
//            Geocoder mGeoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.KOREA);
//            List<Address> startLocation = null;
//            List<Address> endLocation = null;
            markerList = new ArrayList<>();
//            if (!mcourseData.getStartPointAddr().equals("null")) {
//                //지번주소
//                LOGD(TAG, "Course data sp " + mcourseData.getStartPointAddr());
//                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1);
//                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointAddr(), 1).toString());
//            } else if (!mcourseData.getStartPointRoadAddr().equals("null")) {
//                //도로명주소
//                LOGD(TAG, "Course data srRp " + mcourseData.getStartPointRoadAddr());
//                startLocation = mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1);
//                LOGD(TAG, "start : " + mGeoCoder.getFromLocationName(mcourseData.getStartPointRoadAddr(), 1).toString());
//            }
//
//            if (!mcourseData.getEndPointAddr().equals("null")) {
//                //지번주소
//                LOGD(TAG, "Course ep " + mcourseData.getEndPointAddr());
//                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1);
//                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointAddr(), 1).toString());
//
//            } else if (!mcourseData.getEndPointRoadAddr().equals("null")) {
//                //도로명주소
//                LOGD(TAG, "Course data eRp " + mcourseData.getEndPointRoadAddr());
//                endLocation = mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1);
//                LOGD(TAG, "end : " + mGeoCoder.getFromLocationName(mcourseData.getEndPointRoadAddr(), 1).toString());
//            }
//            markerList.add(new LatLng(startLocation.get(0).getLatitude(), startLocation.get(0).getLongitude()));
//            markerList.add(new LatLng(endLocation.get(0).getLatitude(), endLocation.get(0).getLongitude()));
//            LOGD(TAG, "end : " + endLocation.get(0).getLatitude() + " " + endLocation.get(0).getLongitude());
//            LOGD(TAG, "estar : " + startLocation.get(0).getLatitude() + " " + startLocation.get(0).getLongitude());
//            LOGD(TAG, "size ; " + markerList.size());






        LatLng sPoint = new LatLng(mcourseData.getsLat(), mcourseData.getsLng());
        LatLng ePoint = new LatLng(mcourseData.geteLat(), mcourseData.geteLng());
        LOGD(TAG, sPoint.toString());
        LOGD(TAG, ePoint.toString());
        markerList.add(sPoint);
        markerList.add(ePoint);



            for (int position = 0; position < markerList.size(); position++) {
                String title;
                if (position == 0)
                    title = "start";
                else
                    title = "end";
                mgoogleMap.addMarker(new MarkerOptions().position(markerList.get(position)).title(title).icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, getActivity())));
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerList.get(0)).include(markerList.get(1));
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = 300;
            int padding = 50;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mgoogleMap.moveCamera(cu);
//        } catch (IOException e) {
//            e.printStackTrace();
//            LOGD(TAG, "cannot find location info");
//        }

//부가정보
        getPlaceID(mcourseData);

    }

    private void getPlaceDetails(String place_id) {



        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                        .getApiService();
        LOGD(TAG,"dd"+markerList.toString());
        if (markerList!=null) {

            final Call<PlaceDetails> responseBodyCall =
                    apiService.callPlaceDetails(place_id,
                            ApiHelper.getQueryFeilds(ApiHelper.PLACE_DETAIL_FEILDS_REQUEST),
                            ApiService.GOOGLE_MAPS_API_SERVICE_KEY);
            responseBodyCall.enqueue(new Callback<PlaceDetails>() {
                @Override
                public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                    if (response.isSuccessful() && response.body().getStatus().equals("OK")) {
                        LOGD(TAG, "server contacted at: " + call.request().url());

                        placeDetails = response.body();
                        //photo정보 있는경우 주소 가져오기
                        if (placeDetails.getResult().getPhotos().size() > 0) {
                            for (int i = 0; i < placeDetails.getResult().getPhotos().size(); i++) {
                                LOGD(TAG, "get ref photos : " + placeDetails.getResult().getPhotos().get(i).getPhotoReference());
                            }
                            //부가정보 사진 //TODO :
                            CoursePlaceInfoAdapter adapterP = new CoursePlaceInfoAdapter(placeDetails.getResult(), getContext(), 0);
                            recyclerView = mView.findViewById(R.id.rv_course_detail_info_photos);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapterP);
                        }
                        //review정보있는경우 넘기기기
                        if (placeDetails.getResult().getReviews().size() > 0) {
                            CoursePlaceInfoAdapter adapterR = new CoursePlaceInfoAdapter(placeDetails.getResult(), getContext(), 1);
                            recyclerView = mView.findViewById(R.id.rv_course_detail_info_reviews);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapterR);
                        }

                        if (placeDetails.getResult().getRating() != null) {
                            //TODO  :  UI기본상태 gone 으로 바꿀것
                            mView.findViewById(R.id.tv_course_detail_info_AVGratings_title_additional).setVisibility(View.VISIBLE);
                            avgRates.setText(placeDetails.getResult().getRating().toString());
                        }

                        if (placeDetails.getResult().getFormattedPhoneNumber() != null) {
                            mView.findViewById(R.id.tv_course_detail_info_numbers_title_additional).setVisibility(View.VISIBLE);
//                            phone.setText(placeDetails.getResult().getFormattedPhoneNumber());
                            btPhone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 전화 연결
                                    String receiver = placeDetails.getResult().getFormattedPhoneNumber();
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + receiver));
                                    startActivity(intent);
                                }
                            });

                        }

                        if (placeDetails.getResult().getWebsite() != null) {
                            mView.findViewById(R.id.tv_course_detail_info_website_title_additional).setVisibility(View.VISIBLE);
//                            website.setText(placeDetails.getResult().getWebsite());
                            btWebsite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = placeDetails.getResult().getWebsite();
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        //부가정보 숨김
                        mView.findViewById(R.id.cv_course_detail_info_add_cardview).setVisibility(View.GONE);

                    }





                }

                @Override
                public void onFailure(Call<PlaceDetails> call, Throwable t) {

                }
            });

        }




    }

    //RUN 버튼 눌렀을 때
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (!PermissionUtil.shouldAskPermission(getActivity(), PERMISSION)) {
            //권한 있음
//            mRun.setVisibility(view.GONE);
//            RunningFragment runningFragment = new RunningFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.homeContainer, runningFragment, "RUNNINGFRAGMENT")
//                    .addToBackStack(null)
//                    .commit();

            //get the last known location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                Location mCurrentLocation = new Location(location.getProvider());
                                mCurrentLocation.setLatitude(location.getLatitude());
                                mCurrentLocation.setLongitude(location.getLongitude());
                                Intent intent = new Intent(getActivity(), RunningActivity.class);
                                //현재위치좌표를 같이 넘겨줌
                                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                                intent.putExtra("longitude", mCurrentLocation.getLongitude());
                                startActivity(intent);

                            }
                        }
                    });


        } else
            PermissionUtil.checkPermission(getActivity(), PERMISSION, REQUEST_LOCATION_PERMISSION);
    }

    public void getPhotoImg() {
        //img 받아온 후 glide사용



    }

    public interface detailFragListener{
        public void onBackPressed ();
    }
}

package smu.ac.kr.johnber.course;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class CourseDetailFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private final static String TAG = makeLogTag(CourseDetailFragment.class);
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private TextView courseName;
    private TextView startPoint;
    private TextView endPoint;
    private TextView distance;
    private TextView calories;
    private TextView time;
    private TextView course;
    private TextView courseInfo;
    private TextView avgRates;
    private TextView phone;
    private ImageView btPhone;
    private ImageView btWebsite;
    private TextView website;
    private ScrollView scrollView;
    private Button mRun;
    private RecyclerView recyclerView;
    private View mView;
    private View rootView;
    private MapView mMapView;
    private GoogleMap mgoogleMap;
    private Realm mRealm;
    private int dataNO;
    private RunningCourse mcourseData;
    private ArrayList<LatLng> markerList;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlaceDetails placeDetails;

    private detailFragListener callbacklistener;
    private ApiCallback callback;

    public CourseDetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        mRun = mView.findViewById(R.id.btn_course_run);
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
        rootView = mView.findViewById(R.id.courseContainers);

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

    private void getPlaceID(final RunningCourse course, String query, final ApiCallback callback) {
        //get place id from google place search

        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                        .getApiService();
        if (markerList != null) {

            final Call<ResponseBody> responseBodyCall =
                    apiService.callPlaceSearch(query, "textquery",
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
                            String place_id = candidates.getString("place_id");
                            if (candidates.has("photos")) {
//                                photoref = candidates.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                            }
//                            LOGD(TAG, "place address: " +course.getStartPoint()+":"+ formatted_address);
//                            LOGD(TAG, "place id is  r: " + course.getStartPoint() + " : " + place_id);
//                            LOGD(TAG, "photo reference: " +course.getStartPoint()+":"+ photoref);
                            //
                            if (!place_id.equals(null)) {
                                callback.onSuccess(place_id);
//                                getPlaceDetails(place_id);
                            }
                        } else {
                            if (status.equals("ZERO_RESULTS")) {
//                                callback.onZERORESUT();
                            }
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

        if (callbacklistener != null) {
            callbacklistener.onBackPressed();
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mgoogleMap = googleMap;


        markerList = new ArrayList<>();

        if (!mcourseData.getEndPointAddr().equals("null")) {

            LatLng sPoint = new LatLng(mcourseData.getsLat(), mcourseData.getsLng());
            LatLng ePoint = new LatLng(mcourseData.geteLat(), mcourseData.geteLng());
//            LOGD(TAG, "detail start point : " + sPoint.toString());
//            LOGD(TAG, "detail start point : " + ePoint.toString());
            markerList.add(sPoint);
            markerList.add(ePoint);


            for (int position = 0; position < markerList.size(); position++) {

                String title;
                if (position == 0)
                    title = mcourseData.getStartPoint();
                else
                    title = mcourseData.getEndPoint();

                mgoogleMap.addMarker(new MarkerOptions().position(markerList.get(position)).title(title).icon(BitmapUtil.getBitmapDescriptor(R.drawable.ic_marker_current, getActivity())));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerList.get(0)).include(markerList.get(1));
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = 300;
            int padding = 50;

            CameraUpdate cu;

            if (sPoint.equals(ePoint)) {

                //시작-종료지점 같은경우 zoom level 조정
                cu = CameraUpdateFactory.newLatLngZoom(sPoint, 18);

            } else {
                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            }
            mgoogleMap.moveCamera(cu);


//부가정보
            final String[] tmpid = new String[1];
            callback = new ApiCallback() {

                @Override
                public void onSuccess(String place_id) {
                    if (!place_id.equals(null))
                        getPlaceDetails(place_id, new ApiDetailCallback() {
                            @Override
                            public void onDetailSuccessed(PlaceDetails details) {
                                setRecyclerview(details);
                            }
                        });
                    tmpid[0] = place_id;
                }

                @Override
                public void onZERORESUT() {
                    // 시작명으로 다시 검색
//
                }
            };

            getPlaceID(mcourseData, mcourseData.getStartPoint(), callback);
            if (tmpid.equals(null)) {
                getPlaceID(mcourseData, mcourseData.getEndPoint(), callback);

            }

        }
    }

    private void setRecyclerview(final PlaceDetails placeDetails) {


        if (placeDetails.getResult().getPhotos() != null) {
            for (int i = 0; i < placeDetails.getResult().getPhotos().size(); i++) {
//                LOGD(TAG, "get ref photos : " + placeDetails.getResult().getPhotos().get(i).getPhotoReference());
            }

            //부가정보 사진
            CoursePlaceInfoAdapter adapterP = new CoursePlaceInfoAdapter(placeDetails.getResult(), getContext(), 0);
            recyclerView = mView.findViewById(R.id.rv_course_detail_info_photos);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapterP);
        }

        if (placeDetails.getResult().getReviews() != null) {
            CoursePlaceInfoAdapter adapterR = new CoursePlaceInfoAdapter(placeDetails.getResult(), getContext(), 1);
            recyclerView = mView.findViewById(R.id.rv_course_detail_info_reviews);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapterR);
        }

        if (placeDetails.getResult().getRating() != null) {
            mView.findViewById(R.id.tv_course_detail_info_AVGratings_title_additional).setVisibility(View.VISIBLE);
            avgRates.setText(placeDetails.getResult().getRating().toString());
        }

        if (placeDetails.getResult().getFormattedPhoneNumber() != null) {
            mView.findViewById(R.id.tv_course_detail_info_numbers_title_additional).setVisibility(View.VISIBLE);
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
            btWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = placeDetails.getResult().getWebsite();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }

        if (placeDetails.getStatus().equals("ZERO_RESULTS")
                || placeDetails.getStatus().equals("ZERO_RESULT")
                || placeDetails.getResult().getPhotos() == null)

            mView.findViewById(R.id.cv_course_detail_info_add_cardview).setVisibility(View.GONE);

    }

    private void getPlaceDetails(String place_id, final ApiDetailCallback detailCallback) {


        ApiService apiService =
                ApiServiceGenerator.getApiServiceGenerator(ApiServiceGenerator.BASE_URL_TYPE_PLACE)
                        .getApiService();

        if (markerList != null) {

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
                        if (placeDetails.getResult() != null) {
                            detailCallback.onDetailSuccessed(placeDetails);
                        }
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

                                //XXX : Course - RUN - 해당 코스 정보 받아와서  split [ realmUID(코스)/행정구역(getfromlocation) ]

                                //현재위치좌표를 같이 넘겨줌
                                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                                intent.putExtra("longitude", mCurrentLocation.getLongitude());

                                intent.putExtra("fromCourse", true);
                                //코스 시작지점, 종료지점 좌표
                                intent.putExtra("course_slat", mcourseData.getsLat());
                                intent.putExtra("course_slng", mcourseData.getsLng());
                                intent.putExtra("course_elat", mcourseData.geteLat());
                                intent.putExtra("course_elng", mcourseData.geteLng());

                                //코스 - 좌표 명
                                intent.putExtra("course_sName", mcourseData.getStartPoint());
                                intent.putExtra("course_eName", mcourseData.getEndPoint());
                                startActivity(intent);
                            }
                        }
                    });


        } else
            PermissionUtil.checkPermission(getActivity(), PERMISSION, REQUEST_LOCATION_PERMISSION);
    }


    public interface detailFragListener {
        public void onBackPressed();
    }

    public interface ApiCallback {
        void onSuccess(String place_id);

        void onZERORESUT();


    }

    public interface ApiDetailCallback {
        void onDetailSuccessed(PlaceDetails details);
    }

}





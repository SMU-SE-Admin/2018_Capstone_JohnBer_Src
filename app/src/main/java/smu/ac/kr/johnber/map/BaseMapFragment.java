package smu.ac.kr.johnber.map;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.util.LogUtils;
import smu.ac.kr.johnber.util.PermissionUtil;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseMapFragment extends Fragment implements OnMapReadyCallback {
  private View mBaseView;
  private GoogleMap mgoogleMap;
  private MapView mBaseMapView;
  private static final String TAG = LogUtils.makeLogTag(BaseMapFragment.class);

  public BaseMapFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBaseView =  inflater.inflate(R.layout.map_fragment, container, false);
    LOGD(TAG,"onCreateVeiw");
    if (savedInstanceState == null) {
      /**
       * view 초기화 및 MapView 추가
       */
      initView();
      mBaseMapView = mBaseView.findViewById(R.id.base_mapview);
      mBaseMapView.onCreate(savedInstanceState);
      mBaseMapView.onResume();
      mBaseMapView.getMapAsync(this);

    }

    return mBaseView;
  }

  //TODO : 오버라이드할것
  protected void initView() {
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    LOGD(TAG,"onAttached");

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {

    mgoogleMap = googleMap;
    // GPS : 확인
    mgoogleMap = googleMap;
    googleMap.setMinZoomPreference(17);
    LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 3));
//        mgoogleMap.setMyLocationEnabled(true);
    LOGD(TAG,"BASEMAP");
    }

    public MapView getBaseMapView () {
        return mBaseMapView;
    }
    public GoogleMap getGoogleMap() {
      return mgoogleMap;
    }
}

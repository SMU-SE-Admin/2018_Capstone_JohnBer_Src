package smu.ac.kr.johnber.map;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.util.PermissionUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseMapFragment extends Fragment implements OnMapReadyCallback {


  private GoogleMap mgoogleMap;

  public BaseMapFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.map_fragment, container, false);

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mgoogleMap = googleMap;
    googleMap.setMinZoomPreference(17);
    LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,18));
  }
}

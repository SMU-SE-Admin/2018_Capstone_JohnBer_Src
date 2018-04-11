package smu.ac.kr.johnber.map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import smu.ac.kr.johnber.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseMapFragment extends Fragment implements OnMapReadyCallback {


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
  public void onMapReady(GoogleMap googleMap) {

  }
}

package smu.ac.kr.johnber.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import smu.ac.kr.johnber.run.JBLocation;

/**
 * android.location <-> johnber.location
 */
public class LocationUtil {

    // v JBLocation to android.Location
    public static Location jbLocationToLocation(JBLocation jb) {
        Location location = new Location("");
        location.setLatitude(jb.getmLatitude());
        location.setLongitude(jb.getmLongitude());
        location.setTime(jb.getmTime());
        return location;
    }

    // JBLocation to android.LatLang
    public static LatLng jbLocationToLatLng(JBLocation jb) {
        return  new LatLng(jb.getmLatitude(), jb.getmLongitude());
    }

    // android.Location to JBlocation
    public static JBLocation locationTojbLocation(Location location) {
        JBLocation jb = new JBLocation();
        jb.setmLatitude(location.getLatitude());
        jb.setmLongitude(location.getLongitude());
        jb.setmTime(location.getTime());
        return jb;
    }

    // android.LatLang to JBlocation
    public static JBLocation LatLangTojbLocation(LatLng latLng) {
        JBLocation jb = new JBLocation();
        jb.setmLatitude(latLng.latitude);
        jb.setmLongitude(latLng.longitude);

        return jb;
    }
    // Location to LatLang

    // LatLang to Location

    public static String latlngtoStringLocation(JBLocation location, Context context) {
        String stringLocation=null;
        Geocoder mGeoCoder = new Geocoder(context, Locale.KOREA);
        List<Address> address;

        try {
            address = mGeoCoder.getFromLocation(location.getmLatitude(), location.getmLongitude(), 1);
            if(address != null &&address.size()>0){
                stringLocation = address.get(0).getAddressLine(0).toString();
                Log.d("마이코스 시작위치 ", stringLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringLocation;
    }
}

package smu.ac.kr.johnber.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import smu.ac.kr.johnber.map.JBLocation;

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


}

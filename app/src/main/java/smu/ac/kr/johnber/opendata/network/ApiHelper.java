package smu.ac.kr.johnber.opendata.network;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class ApiHelper {
    public static final int PLACE_SEARCH_FEILDS_REQUEST = 1101;
    public static final int PLACE_DETAIL_FEILDS_REQUEST = 1102;
    public static final int PLACE_PHOTO_MAXHEIGHT = 400;

    // Place Search
    // key, input(location name), inputtype=textquery)
    // language
    // fields=photos(for photo reference), place_id
    //locationbiase = point:lat,lang
    //key=
    //https://square.github.io/retrofit/2.x/retrofit/index.html?retrofit2/http/QueryMap.html

    //* place_id is required
    //place_id
    //fields=opening_hours,formatted_phone_number,website,review,rating

    public static String getQueryFeilds(int code){
        String fields  = null;
        switch (code) {
            case PLACE_SEARCH_FEILDS_REQUEST :
                fields = "formatted_address,photos,place_id";
                break;

            case PLACE_DETAIL_FEILDS_REQUEST:
                fields = "name,rating,review,formatted_phone_number" +
                        ",url,photo,opening_hours,website,geometry";
                break;

        }

        return fields;
    }

    public static String getLocationbiase(LatLng lat) {
        return "point:"+lat.latitude+","+lat.longitude;
    }


}

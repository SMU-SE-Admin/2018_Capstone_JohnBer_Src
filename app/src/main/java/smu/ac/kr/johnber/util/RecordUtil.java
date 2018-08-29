package smu.ac.kr.johnber.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordUtil {
    /**
     * data format 변경
     * 칼로리 계산
     * ...
     */

    /**
     * @link 계산 공식 참고 http://117.16.42.60/redmine/issues/1063#
     * 7MET 계산(달리기 가중치7.0)
     */

    /***************
     칼로리 계산은 MET을 이용.
     일반적인 조깅은 7MET
     1MET(신진대사 해당치) = 1*3.5ml/kg/min -> 결과는 ml 단위의 산소가 나옴.
     산소 1L 당 5kcal 소모

     formula : 7 * (3.5 * kg * min) * 5/1000

     출처 : https://m.blog.naver.com/PostView.nhn?blogId=jmkimz&logNo=220137752732&categoryNo=22&proxyReferer=https%3A%2F%2Fwww.google.com%2F
     ****************/
    //jgh
    public static double getAverageCalories(double weight, double workoutTime) {
<<<<<<< HEAD
        //시간 변경. if 14:50 -> 14.83333
        double seconds = (workoutTime / 1000) % 60 *1/60;
        double min = workoutTime/(1000*60) + seconds;
        //LogUtils.LOGD("RecordUtil ", Integer.toString(min));
        double calories = 7 * (3.5 * weight * min) * 5/1000;
        Log.d("mainactivity", "RecordUtil calculate calories : " + calories + " "+ weight + " " + workoutTime);
=======
        int minutes = millisecondsToMinute(workoutTime);
//        LogUtils.LOGD("RecordUtil ", Integer.toString(minutes));
        double calories = 5 * (7 * (3.5 * weight * minutes) / 1000);
>>>>>>> 1cdfbe563869b247caaa6b8c46398a8554a14939
        return calories;
    }

    public static int millisecondsToMinute(double elapsedTime) {
        int seconds = (int) (elapsedTime / 1000);
        int minutes = (seconds / 60) % 60;
        return minutes;
    }

    public static double metersToKillometers(double distance) {
        return distance / 1000;
    }

    public static String distanceToStringFormat(double distance) {
        String formmatedString = String.format("%.2f", distance);
        return formmatedString;
    }

    public static String milliseconsToStringFormat(double ms) {
        int seconds = (int) ((ms) / 1000);
        int minutes = (seconds / 60) % 60;
        int hours = (minutes/60)%60;
        seconds = seconds % 60;

        String stringTime ="";
        if (hours > 0) {
            stringTime=String.format("%02d", hours)+":";
        }
        stringTime += String.format("%02d", minutes);
        stringTime += ":" + String.format("%02d", seconds);

        return stringTime;
    }

    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }

    //km 단위
    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

}
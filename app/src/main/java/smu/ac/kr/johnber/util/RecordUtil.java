package smu.ac.kr.johnber.util;

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
    public static double getAverageCalories(double weight, double workoutTime) {
        int minutes = millisecondsToMinute(workoutTime);
        LogUtils.LOGD("RecordUtil ", Integer.toString(minutes));
        double calories = 5 * (7 * (3.5 * weight * minutes) / 1000);
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
        seconds = seconds % 60;
        String stringTime = "";
        stringTime += "" + String.format("%02d", minutes);
        stringTime += ":" + String.format("%02d", seconds);

        return stringTime;
    }

    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }
}
package smu.ac.kr.johnber.util;

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
    public static double getAverageCalories(double weight, double workoutTime){
        int minutes = millisecondsToMinute(workoutTime);
        LogUtils.LOGD("RecordUtil ",Integer.toString(minutes));
        double calories=5*(7*(3.5*weight*minutes)/1000);
        return calories;
    }

    public static int millisecondsToMinute(double elapsedTime) {
        int seconds = (int) (elapsedTime / 1000);
        int minutes = (seconds / 60)%60;
        return minutes;
    }

    public static double metersToKillometers(double distance) {
        return distance/1000;
    }
}

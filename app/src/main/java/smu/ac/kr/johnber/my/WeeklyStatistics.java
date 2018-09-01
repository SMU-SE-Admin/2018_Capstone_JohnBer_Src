package smu.ac.kr.johnber.my;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.DateUtil;

public class WeeklyStatistics {
    //JGH
    public static List<Double> weeklyStats(HashMap<String, Record> recordHashMap) {
        //현재 주차 구하기.
        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);

        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        //현재 시간 받아오기.
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = sdf.format(date);

        List<Double> weeklyKM = new ArrayList<Double>();
        List<Double> weeklyTime = new ArrayList<Double>();
        List<Double> weeklyCal = new ArrayList<Double>();

        int rangeDateOfWeek [] = new int[7];
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        String yyyyMM = sdf1.format(date);
        rangeDateOfWeek = DateUtil.getRangeDateOfWeek(yyyyMM, weekOfMonth);

        int dayOfWeek = DateUtil.dayOfWeekForWeeklyStatistics(todayDate);

        Iterator<String> iter = recordHashMap.keySet().iterator();
        while(iter.hasNext()){
            //ex) keys : hashmap의 key, 2018-08-23/07:22:31
            String keys = (String) iter.next();
            String dates = keys.split("/")[0];
            //날짜 따오기. 23
            String day = dates.split("-")[2];
            //Log.d("mainactivity", "keys, dates, times: " + dates.toString());

            for (int i=0;i<rangeDateOfWeek.length;i++){
                if (rangeDateOfWeek[i] == Integer.parseInt(day)){
                    Record dailyRecord = recordHashMap.get(keys);

                    weeklyKM.add(dailyRecord.getDistance());
                    weeklyTime.add(dailyRecord.getElapsedTime());
                    weeklyCal.add(dailyRecord.getCalories());
                }
            }
        }

        List<Double> stats = new ArrayList<>();
        stats.add(calculateTime(weeklyTime,dayOfWeek));
        stats.add(calculateKcal(weeklyCal,dayOfWeek));
        stats.add(calculateKM(weeklyKM,dayOfWeek));
        return stats;
    }

    public static double calculateKcal(List<Double> kcalList, int dayOfWeek){
        double sum=0;
        for(int i=0; i<kcalList.size(); i++){
            sum+=kcalList.get(i);
        }
        if (dayOfWeek != 0) {
            sum = sum / dayOfWeek;
        } else{
            sum = sum/7;
        }

        Log.d("MAINACTIVITY", "weeklykcal : " + sum);
    return sum;
    }

    public static double calculateTime(List<Double> timeList, int dayOfWeek){
        double sum =0;
        for(int i=0; i<timeList.size(); i++){
            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sum += timeList.get(i);
        }

        if (dayOfWeek != 0) {
            sum = sum / dayOfWeek;
        } else{
            sum = sum/7;
        }

        int seconds = (int) (sum / 1000) % 60 ;            //초
        int minutes = (int) ((sum/ (1000*60)) % 60);  //분
        int hours   = (int) ((sum / (1000*60*60)) % 24);//시

        Log.d("MAINACTIVITY", "weeklyTIME : " + String.format("%02d h:%02d m:%02d s", hours, minutes, seconds));
        return sum;
    }

    public static double calculateKM(List<Double> kmList, int dayOfWeek){
        double sum=0;
        for(int i=0; i<kmList.size(); i++){
            sum+=kmList.get(i);
        }

        if (dayOfWeek != 0) {
            sum = sum / dayOfWeek;
        } else{
            sum = sum/7;
        }
        Log.d("MAINACTIVITY", "weeklyKM : " + sum);
        return sum;
    }
}

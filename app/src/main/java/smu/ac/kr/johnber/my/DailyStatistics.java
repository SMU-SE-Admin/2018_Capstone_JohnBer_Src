package smu.ac.kr.johnber.my;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import smu.ac.kr.johnber.run.Record;

public class DailyStatistics {
    //JGH
    public static void dailyStats(HashMap<String, Record> recordHashMap) {
        //현재 시간 받아오기.
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = sdf.format(date);

        List<Double> dailyKM = new ArrayList<Double>();
        List<Double> dailyTime = new ArrayList<Double>();
        List<Double> dailyCal = new ArrayList<Double>();


        Iterator<String> iter = recordHashMap.keySet().iterator();
        while(iter.hasNext()){
            //Log.d("mainactivity", "dkdkdkdkdkdkdkd : " + recordHashMap.keySet());
            //ex) keys : hashmap의 key, 2018
            String keys = (String) iter.next();
            String dates = keys.split("/")[0];
            String times = keys.split("/")[1];

            //Log.d("mainactivity", "keys, dates, times: " + dates.toString());

            if (todayDate.equals(dates)){
                Record dailyRecord = recordHashMap.get(keys);

                dailyKM.add(dailyRecord.getDistance());
                dailyTime.add(dailyRecord.getElapsedTime());
                dailyCal.add(dailyRecord.getCalories());

                //Log.d("mainactivity", "daily: " + dailyKM.toString());
            }
        }
        calculateTime(dailyTime);
        calculateKcal(dailyCal);
        calculateKM(dailyKM);
    }

    public static void calculateKcal(List<Double> kcalList){
        double sum=0;
        for(int i=0; i<kcalList.size(); i++){
            sum+=kcalList.get(i);
        }
        Log.d("MAINACTIVITY", "kcal" + sum);

    }

    public static void calculateTime(List<Double> timeList){
        double sum =0;
        for(int i=0; i<timeList.size(); i++){
            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sum += timeList.get(i);
        }

        int seconds = (int) (sum / 1000) % 60 ;            //초
        int minutes = (int) ((sum/ (1000*60)) % 60);  //분
        int hours   = (int) ((sum / (1000*60*60)) % 24);//시

        Log.d("MAINACTIVITY", "TIME : " + String.format("%02d h:%02d m:%02d s", hours, minutes, seconds));
    }

    public static void calculateKM(List<Double> kmList){
        double sum=0;
        for(int i=0; i<kmList.size(); i++){
            sum+=kmList.get(i);
        }
        Log.d("MAINACTIVITY", "KM" + sum);
    }
}

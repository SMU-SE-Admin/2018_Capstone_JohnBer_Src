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

public class MonthlyStatistics {
    //JGH
    public static void monthlyStats(HashMap<String, Record> recordHashMap) {
        //현재 시간 받아오기.
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yyyyMM = sdf.format(date);

        List<Double> monthlyKM = new ArrayList<Double>();
        List<Double> monthlyTime = new ArrayList<Double>();
        List<Double> monthlyCal = new ArrayList<Double>();

        int numberOfRecord = 0;

        Iterator<String> iter = recordHashMap.keySet().iterator();
        while(iter.hasNext()){
            //ex) keys : hashmap의 key, 2018-08-23/07:22:31
            String keys = (String) iter.next();
            String dates = keys.split("/")[0];
            //년월 따오기. 201808
            String year = dates.split("-")[0];
            String month = dates.split("-")[1];
            dates = year + month;

            //Log.d("mainactivity", "keys, dates, times: " + dates.toString());

            if (dates.equals(yyyyMM)){
                Record dailyRecord = recordHashMap.get(keys);

                monthlyKM.add(dailyRecord.getDistance());
                monthlyTime.add(dailyRecord.getElapsedTime());
                monthlyCal.add(dailyRecord.getCalories());

                numberOfRecord ++;

                //Log.d("mainactivity", "daily: " + dailyKM.toString());
            }
        }

        calculateTime(monthlyTime, numberOfRecord);
        calculateKcal(monthlyCal, numberOfRecord);
        calculateKM(monthlyKM, numberOfRecord);
    }

    public static void calculateKcal(List<Double> kcalList, int numberOfRecord){
        double sum=0;
        for(int i=0; i<kcalList.size(); i++){
            sum+=kcalList.get(i);
        }

        sum = sum/numberOfRecord;


        Log.d("MAINACTIVITY", "monthly_weeklykcal : " + sum);

    }

    public static void calculateTime(List<Double> timeList, int numberOfRecord){
        double sum =0;
        for(int i=0; i<timeList.size(); i++){
            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sum += timeList.get(i);
        }

        sum = sum/numberOfRecord;

        int seconds = (int) (sum / 1000) % 60 ;            //초
        int minutes = (int) ((sum/ (1000*60)) % 60);  //분
        int hours   = (int) ((sum / (1000*60*60)) % 24);//시

        Log.d("MAINACTIVITY", "monthly_TIME : " + String.format("%02d h:%02d m:%02d s", hours, minutes, seconds));
    }

    public static void calculateKM(List<Double> kmList, int numberOfRecord){
        double sum=0;
        for(int i=0; i<kmList.size(); i++){
            sum+=kmList.get(i);
        }

        sum = sum/numberOfRecord;

        Log.d("MAINACTIVITY", "monthly_KM : " + sum);
    }
}

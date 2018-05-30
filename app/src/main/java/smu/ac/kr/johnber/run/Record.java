package smu.ac.kr.johnber.run;


import java.util.ArrayList;
import java.util.Date;

import smu.ac.kr.johnber.map.JBLocation;

/**
 * 운동기록 Model
 */
public class Record {
    //TODO : 시간 포맷 등 바꿔주는 util 필요함
    private double distance;
    private double elapsedTime;
    private double calories;
    // android.JBLocation 대신 커스텀 JBLocation 사용
    private ArrayList<JBLocation> JBLocation;
    private Date date;
    private double startTime;
    private double endTime;
    private String title;

    public Record() {

    }

    public Record(double distance, double elapsedTime, double calories, ArrayList<JBLocation> JBLocation, Date date, double startTime, double endTime, String title) {
        this.distance = distance;
        this.elapsedTime = elapsedTime;
        this.calories = calories;
        this.JBLocation = JBLocation;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public ArrayList<JBLocation> getJBLocation() {
        return JBLocation;
    }

    public void setJBLocation(ArrayList<JBLocation> JBLocation) {
        this.JBLocation = JBLocation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

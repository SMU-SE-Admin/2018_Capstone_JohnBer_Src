package smu.ac.kr.johnber.run;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

/**
 * 운동기록 Model
 */
public class Record {
    //TODO : 시간 포맷 등 바꿔주는 util 필요함
    private double distance;
    private double elapsedTime;
    private double calories;
    private ArrayList<Location> location;
    private Date date;
    private double startTime;
    private double endTime;
    private String title;

    public Record() {

    }

    public Record(double distance, double elapsedTime, double calories, ArrayList<Location> location, Date date, double startTime, double endTime, String title) {
        this.distance = distance;
        this.elapsedTime = elapsedTime;
        this.calories = calories;
        this.location = location;
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

    public ArrayList<Location> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Location> location) {
        this.location = location;
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

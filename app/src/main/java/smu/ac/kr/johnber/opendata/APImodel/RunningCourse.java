package smu.ac.kr.johnber.opendata.APImodel;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class RunningCourse extends RealmObject {

    public RunningCourse() {
    }
//    @SerializedName("시작지점명")
    private String startPoint;
//    @SerializedName("종료지점명")
    private String endPoint;
//    @SerializedName("위도")
//    private double latitude;
//    @SerializedName("경도")
//    private double longitude;
//    @SerializedName("경로정보")
    private String course;
//    @SerializedName("길소개")
    private String courseInfo;
//    @SerializedName("길명")
    @Required
    private String courseName;
//    @SerializedName("총소요시간")
    private double time;
//    @SerializedName("총길이")
    private double distance;
//    @SerializedName("종료지점소재지지번주소")
    private String endpointAddr;
//    @SerializedName("시작지점소재지지번주소")
    private String startPointAddr;

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

//    public double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }
//
//    public double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEndpointAddr() {
        return endpointAddr;
    }

    public void setEndpointAddr(String endpointAddr) {
        this.endpointAddr = endpointAddr;
    }

    public String getStartPointAddr() {
        return startPointAddr;
    }

    public void setStartPointAddr(String startPointAddr) {
        this.startPointAddr = startPointAddr;
    }


}

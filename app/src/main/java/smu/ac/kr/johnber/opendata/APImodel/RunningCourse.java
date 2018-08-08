package smu.ac.kr.johnber.opendata.APImodel;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class RunningCourse extends RealmObject {
    @SerializedName("종료지점명")
    private String endPoint;
    @SerializedName("시작지점명")
    private String startPoint;
    @SerializedName("경로정보")
    private String course;
    @SerializedName("길소개")
    private String courseInfo;
    @SerializedName("길명")
    @Required
    private String courseName;
    @SerializedName("총소요시간")
    private String time;
    @SerializedName("총길이")
    private String distance;
    @SerializedName("시작지점도로명주소")
    private String startPointRoadAddr;
    @SerializedName("종료지점소재지지번주소")
    private String endPointAddr;
    @SerializedName("시작지점소재지지번주소")
    private String startPointAddr;
    @SerializedName("종료지점소재지도로명주소")
    private String endPointRoadAddr;
  
  public RunningCourse() {


    private String length;
    public RunningCourse() {

    }

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
        this.length = Integer.toString( courseInfo.length() + course.length());
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEndPointAddr() {
        return endPointAddr;
    }

    public void setEndPointAddr(String endPointAddr) {
        this.endPointAddr = endPointAddr;
    }

    public String getStartPointAddr() {
        return startPointAddr;
    }

    public void setStartPointAddr(String startPointAddr) {
        this.startPointAddr = startPointAddr;
    }

    public String getStartPointRoadAddr() {
        return startPointRoadAddr;
    }

    public void setStartPointRoadAddr(String startPointRoadAddr) {
        this.startPointRoadAddr = startPointRoadAddr;
    }

    public String getEndPointRoadAddr() {
        return endPointRoadAddr;
    }

    public void setEndPointRoadAddr(String endPointRoadAddr) {
        this.endPointRoadAddr = endPointRoadAddr;
    }





}

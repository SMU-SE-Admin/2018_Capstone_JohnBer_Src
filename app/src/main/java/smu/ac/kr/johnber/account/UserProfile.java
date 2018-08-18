package smu.ac.kr.johnber.account;

public class UserProfile {
    //male : 0
    //female :1
    private String userID;
    private double weight;
    private double height;
    private String gender;
    private int profileIMG;
    private final static int DEFAULT_WEIGHT = 1;
    private final static int DEFAULT_HEIGHT = 1;

    public UserProfile(String userID, double weight, double height, String gender) {
        this.userID = userID;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}

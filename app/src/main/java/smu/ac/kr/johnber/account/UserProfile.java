package smu.ac.kr.johnber.account;


import java.lang.String;

public class UserProfile{
    //male : 0
    //female :1
    private String userId, gender;
    private double weight, height;
    //private int profileIMG;
    //private final static int DEFAULT_WEIGHT = 1;
    //private final static int DEFAULT_HEIGHT = 1;

    public UserProfile(String userId, double weight, double height, String gender){
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }


    public UserProfile() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

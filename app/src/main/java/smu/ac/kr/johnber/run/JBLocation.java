package smu.ac.kr.johnber.run;

public class JBLocation {

    public JBLocation() {
        setmTime(0);
    }

    private double mLatitude;
    private double mLongitude;
    private long mTime;

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }


}

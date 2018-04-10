package smu.ac.kr.johnber.opendata.weatherforecastmodel;

/**
 * Created by yj34 on 25/03/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

  @SerializedName("baseDate")
  @Expose
  private String baseDate;
  @SerializedName("baseTime")
  @Expose
  private String baseTime;
  @SerializedName("category")
  @Expose
  private String category;
  @SerializedName("nx")
  @Expose
  private int nx;
  @SerializedName("ny")
  @Expose
  private int ny;
  @SerializedName("obsrValue")
  @Expose
  private Double obsrValue;

  public String getBaseDate() {
    return baseDate;
  }

  public void setBaseDate(String baseDate) {
    this.baseDate = baseDate;
  }

  public String getBaseTime() {
    return baseTime;
  }

  public void setBaseTime(String baseTime) {
    this.baseTime = baseTime;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public double getNx() {
    return nx;
  }

  public void setNx(Integer nx) {
    this.nx = nx;
  }

  public double getNy() {
    return ny;
  }

  public void setNy(Integer ny) {
    this.ny = ny;
  }

  public Double getObsrValue() {
    return obsrValue;
  }

  public void setObsrValue(Double obsrValue) {
    this.obsrValue = obsrValue;
  }

}
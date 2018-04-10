package smu.ac.kr.johnber.opendata.weatherforecastmodel;

/**
 * Created by yj34 on 25/03/2018.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header {

  @SerializedName("resultCode")
  @Expose
  private String resultCode;
  @SerializedName("resultMsg")
  @Expose
  private String resultMsg;

  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getResultMsg() {
    return resultMsg;
  }

  public void setResultMsg(String resultMsg) {
    this.resultMsg = resultMsg;
  }

}
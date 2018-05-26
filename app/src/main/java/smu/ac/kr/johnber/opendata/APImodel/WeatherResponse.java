package smu.ac.kr.johnber.opendata.APImodel;

/**
 * Created by yj34 on 25/03/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

  @SerializedName("response")
  @Expose
  private Response response;

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

}
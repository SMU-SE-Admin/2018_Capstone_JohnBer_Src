package smu.ac.kr.johnber.opendata.weatherforecastmodel;

/**
 * Created by yj34 on 25/03/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

  @SerializedName("header")
  @Expose
  private Header header;
  @SerializedName("body")
  @Expose
  private Body body;

  public Header getHeader() {
    return header;
  }

  public void setHeader(Header header) {
    this.header = header;
  }

  public Body getBody() {
    return body;
  }

  public void setBody(Body body) {
    this.body = body;
  }

}
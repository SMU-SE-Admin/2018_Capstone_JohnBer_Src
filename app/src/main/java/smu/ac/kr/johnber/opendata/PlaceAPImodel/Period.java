package smu.ac.kr.johnber.opendata.PlaceAPImodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {

    @SerializedName("open")
    @Expose
    private Open open;

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }

}

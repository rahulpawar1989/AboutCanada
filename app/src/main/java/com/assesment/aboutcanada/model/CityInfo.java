
package com.assesment.aboutcanada.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityInfo {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("rows")
    @Expose
    private ArrayList<CityInfoRow> cityInfoRows = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<CityInfoRow> getCityInfoRows() {
        return cityInfoRows;
    }

    public void setCityInfoRows(ArrayList<CityInfoRow> cityInfoRows) {
        this.cityInfoRows = cityInfoRows;
    }

}

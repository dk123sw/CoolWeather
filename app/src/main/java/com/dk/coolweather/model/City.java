package com.dk.coolweather.model;

/**
 * Created by lenovo on 2016/8/22.
 * 建立City的实体类
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provincedId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvincedId() {
        return provincedId;
    }

    public void setProvincedId(int provincedId) {
        this.provincedId = provincedId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

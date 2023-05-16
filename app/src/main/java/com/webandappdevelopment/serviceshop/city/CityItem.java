package com.webandappdevelopment.serviceshop.city;

public class CityItem {
    int cityId;
    String cityName;
    String stateName;

    public CityItem(int cityId, String cityName, String stateName) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.stateName = stateName;
    }

    public int getCityId() {
        return cityId;
    }
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
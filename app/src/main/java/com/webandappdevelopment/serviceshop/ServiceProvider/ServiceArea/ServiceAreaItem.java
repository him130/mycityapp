package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceArea;

public class ServiceAreaItem {
    private int Id;
    private String Area;

    public ServiceAreaItem(int id, String area) {
        Id = id;
        Area = area;
    }

    public ServiceAreaItem(String area) {
        Area = area;
    }

    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public String getArea() {
        return Area;
    }
    public void setArea(String area) {
        Area = area;
    }
}

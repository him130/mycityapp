package com.webandappdevelopment.serviceshop.ServiceProviderList;

public class ServiceProviderItem {
    private int Id;
    private int CityId;
    private String Image;
    private String Name;
    private String ServiceList;
    private String Rating;
    private String Contact;
    private String Days;

    public ServiceProviderItem(int id,int cityId, String image, String name, String serviceList, String contact, String days) {
        Id = id;
        CityId = cityId;
        Image = image;
        Name = name;
        Contact = contact;
        ServiceList = serviceList;
        Days = days;
    }


    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public int getCityId() {
        return CityId;
    }
    public void setCityId(int cityId) {
        CityId = cityId;
    }

    public String getContact() {
        return Contact;
    }
    public void setContact(String contact) {
        Contact = contact;
    }

    public String getImage() {
        return Image;
    }
    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getServiceList() {
        return ServiceList;
    }
    public void setServiceList(String serviceList) {
        ServiceList = serviceList;
    }

    public String getRating() {
        return Rating;
    }
    public void setRating(String rating) {
        Rating = rating;
    }

    public String getDays() {
        return Days;
    }
    public void setDays(String days) {
        Days = days;
    }
}

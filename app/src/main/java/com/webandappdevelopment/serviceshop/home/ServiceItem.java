package com.webandappdevelopment.serviceshop.home;

public class ServiceItem {
    private int Id;
    private String Image;
    private String Title;

    public ServiceItem(int id, String image, String title) {
        Id = id;
        Image = image;
        Title = title;
    }

    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public String getImage() {
        return Image;
    }
    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
}

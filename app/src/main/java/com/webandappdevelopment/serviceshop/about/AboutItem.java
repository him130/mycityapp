package com.webandappdevelopment.serviceshop.about;

public class AboutItem {
    private int Id;
    private String VideoUrl;
    private String Title;

    public AboutItem(int id, String videoUrl, String title) {
        Id = id;
        VideoUrl = videoUrl;
        Title = title;
    }

    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
}

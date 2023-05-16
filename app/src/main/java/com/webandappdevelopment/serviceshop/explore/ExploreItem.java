package com.webandappdevelopment.serviceshop.explore;

public class ExploreItem {
    private int Id;
    private String Image;
    private String Title;
    private String VideoUrl;
    private String Type;
    private int SPId;

    public ExploreItem(int id, String title, String videoUrl, String type, int spId) {
        Id = id;
        Title = title;
        VideoUrl = videoUrl;
        Type = type;
        SPId = spId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
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

    public String getVideoUrl() {
        return VideoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public int getSPId() {
        return SPId;
    }
    public void setSPId(int SPId) {
        this.SPId = SPId;
    }
}

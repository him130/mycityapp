package com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos;

public class PhotosItem {
    private int ImageId;
    private String ImageUrl;

    public PhotosItem(int imageId, String imageUrl) {
        ImageId = imageId;
        ImageUrl = imageUrl;
    }

    public int getImageId() {
        return ImageId;
    }
    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

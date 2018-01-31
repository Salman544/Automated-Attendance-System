package com.salman.myproject.rest_api;

/**
 * Created by Salman on 1/1/2018.
 */

public class RecognizeUser {

    private String image;
    private String gallery_name;

    public RecognizeUser(String image, String gallery_name) {
        this.image = image;
        this.gallery_name = gallery_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGallery_name() {
        return gallery_name;
    }

    public void setGallery_name(String gallery_name) {
        this.gallery_name = gallery_name;
    }
}

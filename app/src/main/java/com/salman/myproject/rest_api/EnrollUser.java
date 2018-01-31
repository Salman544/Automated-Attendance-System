package com.salman.myproject.rest_api;

/**
 * Created by Salman on 1/1/2018.
 */

public class EnrollUser {

    private String image;
    private String subject_id;
    private String gallery_name;


    public EnrollUser(String image, String subject_id, String gallery_name) {
        this.image = image;
        this.subject_id = subject_id;
        this.gallery_name = gallery_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getGallery_name() {
        return gallery_name;
    }

    public void setGallery_name(String gallery_name) {
        this.gallery_name = gallery_name;
    }
}

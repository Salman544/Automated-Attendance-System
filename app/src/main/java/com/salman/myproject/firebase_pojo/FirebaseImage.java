package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 1/1/2018.
 */

public class FirebaseImage {

    public String imageName;
    public String imageLink;

    public FirebaseImage() {
    }

    public FirebaseImage(String imageName, String imageLink) {
        this.imageName = imageName;
        this.imageLink = imageLink;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}

package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 1/3/2018.
 */

public class FirebasePropertyDetail {


    public String dealType;
    public String propertyType;
    public int price;
    public String location;
    public String details;
    public String contactNumber;
    public String email;
    public String shortDesc;
    public String photoLink;
    public String key;

    public FirebasePropertyDetail() {
    }

    public FirebasePropertyDetail(String dealType, String propertyType, int price, String location, String details, String contactNumber, String email,String shortDesc) {
        this.dealType = dealType;
        this.propertyType = propertyType;
        this.price = price;
        this.location = location;
        this.details = details;
        this.contactNumber = contactNumber;
        this.email = email;
        this.shortDesc = shortDesc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

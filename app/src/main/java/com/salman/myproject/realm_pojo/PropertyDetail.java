package com.salman.myproject.realm_pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class PropertyDetail extends RealmObject {

    @PrimaryKey
    private int key;

    @Required
    private String dealType;
    private String propertyType;
    private int price;
    private String location;
    private String details;
    private String ref;
    private String propertyId;
    private String contactNumber;
    private String email;
    private String shortDesc;
    private String photoLink;
    private String userKey;
    private String imageKey;

    public PropertyDetail() {
    }

    public PropertyDetail(int key, String dealType, String propertyType, int price, String location, String details, String propertyId, String contactNumber, String email) {
        this.key = key;
        this.dealType = dealType;
        this.propertyType = propertyType;
        this.price = price;
        this.location = location;
        this.details = details;
        this.propertyId = propertyId;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public PropertyDetail(String dealType, String propertyType, int price, String location, String details, String propertyId, String contactNumber, String email) {
        this.dealType = dealType;
        this.propertyType = propertyType;
        this.price = price;
        this.location = location;
        this.details = details;
        this.propertyId = propertyId;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public PropertyDetail(int key, String dealType, String propertyType, int price, String location, String details, String contactNumber, String email) {
        this.key = key;
        this.dealType = dealType;
        this.propertyType = propertyType;
        this.price = price;
        this.location = location;
        this.details = details;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
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

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
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

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
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
}

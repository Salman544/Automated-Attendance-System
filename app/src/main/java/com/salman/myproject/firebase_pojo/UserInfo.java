package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 12/31/2017.
 */

public class UserInfo {

    public String name;
    public String email;
    public String link;
    public String type;

    public UserInfo() {
    }

    public UserInfo(String name, String email, String link,String type) {
        this.name = name;
        this.email = email;
        this.link = link;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

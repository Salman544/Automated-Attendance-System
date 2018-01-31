package com.salman.myproject.realm_pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class RealmStudent extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String name;
    private String rollNumber;
    private String photoLink;
    private String faceId;
    private String courseName;
    private String studentUid;

    public RealmStudent() {
    }

    public RealmStudent(int id, String name, String rollNumber, String photoLink, String faceId, String courseName) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.photoLink = photoLink;
        this.faceId = faceId;
        this.courseName = courseName;
    }

    public RealmStudent(int id, String name, String rollNumber, String photoLink, String faceId, String courseName, String studentUid) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.photoLink = photoLink;
        this.faceId = faceId;
        this.courseName = courseName;
        this.studentUid = studentUid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }
}

package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 1/1/2018.
 */

public class AddStudent {

    public String name;
    public String rollNumber;
    public String photoLink;
    public String faceId;
    public String courseName;
    public String studentUid;

    public AddStudent() {
    }

    public AddStudent(String name, String rollNumber, String photoLink, String faceId, String courseName) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.photoLink = photoLink;
        this.faceId = faceId;
        this.courseName = courseName;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
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
}

package com.salman.myproject.realm_pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Attendance extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String faceId;
    private boolean present;
    private boolean absent;
    private boolean leave;
    private String date;
    private String course;
    private String ref;
    private String studentRef;


    public Attendance() {
    }

    public Attendance(String faceId,String date,String course, boolean present, boolean absent, boolean leave) {
        this.faceId = faceId;
        this.present = present;
        this.absent = absent;
        this.leave = leave;
        this.date = date;
        this.course = course;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public boolean isLeave() {
        return leave;
    }

    public void setLeave(boolean leave) {
        this.leave = leave;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudentRef() {
        return studentRef;
    }

    public void setStudentRef(String studentRef) {
        this.studentRef = studentRef;
    }
}

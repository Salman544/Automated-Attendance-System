package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 1/2/2018.
 */

public class FirebaseAttendance {

    public String faceId;
    public boolean present;
    public boolean absent;
    public boolean leave;
    public String date;
    public String course;
    public String ref;



    public FirebaseAttendance() {
    }

    public FirebaseAttendance(String faceId, boolean present, boolean absent, boolean leave, String date, String course) {
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

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}

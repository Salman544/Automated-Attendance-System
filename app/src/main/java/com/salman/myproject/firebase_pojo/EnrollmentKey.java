package com.salman.myproject.firebase_pojo;

public class EnrollmentKey {

    private String teacherUid;
    private String courseName;
    private String key;

    public EnrollmentKey() {
    }

    public EnrollmentKey(String teacherUid, String courseName) {
        this.teacherUid = teacherUid;
        this.courseName = courseName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(String teacherUid) {
        this.teacherUid = teacherUid;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}

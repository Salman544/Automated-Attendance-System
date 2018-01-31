package com.salman.myproject.pojo;

/**
 * Created by Salman on 1/7/2018.
 */

public class TeacherInfo {

    private String teacherName;
    private String teacherUid;
    private String className;
    private boolean isDeleted;
    public TeacherInfo() {
    }

    public TeacherInfo(String teacherName, String teacherUid, String className) {
        this.teacherName = teacherName;
        this.teacherUid = teacherUid;
        this.className = className;
        isDeleted = false;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(String teacherUid) {
        this.teacherUid = teacherUid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

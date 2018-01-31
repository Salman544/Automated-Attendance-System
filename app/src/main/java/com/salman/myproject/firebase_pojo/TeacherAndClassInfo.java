package com.salman.myproject.firebase_pojo;

/**
 * Created by Salman on 1/7/2018.
 */

public class TeacherAndClassInfo {

    public String teacherName;
    public String teacherUid;
    public String className;

    public TeacherAndClassInfo() {
    }

    public TeacherAndClassInfo(String teacherName, String teacherUid, String className) {
        this.teacherName = teacherName;
        this.teacherUid = teacherUid;
        this.className = className;
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

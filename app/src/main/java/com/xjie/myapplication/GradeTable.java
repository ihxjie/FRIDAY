package com.xjie.myapplication;

public class GradeTable {
    private String courseName,courseMark,courseXf,courseJd,courseProperties;//课程名称 课程成绩 学分 获得的绩点 考试性质

    public String getCourseMark() {
        return courseMark;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseJd() {
        return courseJd;
    }

    public String getCourseXf() {
        return courseXf;
    }

    public String getCourseProperties() {
        return courseProperties;
    }

    public void setCourseMark(String courseMark) {
        this.courseMark = courseMark;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseJd(String courseJd) {
        this.courseJd = courseJd;
    }

    public void setCourseXf(String courseXf) {
        this.courseXf = courseXf;
    }

    public void setCourseProperties(String courseProperties) {
        this.courseProperties = courseProperties;
    }

    public GradeTable(String courseName, String courseMark, String courseXf, String courseJd, String courseProperties){
        this.courseName = courseName;
        this.courseMark = courseMark;
        this.courseXf = courseXf;
        this.courseJd = courseJd;
        this.courseProperties = courseProperties;
    }
}

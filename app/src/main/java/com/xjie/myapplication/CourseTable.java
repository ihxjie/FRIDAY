package com.xjie.myapplication;

public class CourseTable {

    private String time;//第几节上课
    private String place;//在哪里上课
    private String teacher;//老师
    private String course;//课程名称
    private String week;//哪几周上课
    private String campus;//校区
    private String xqj;//星期几

    public String getCourse() {
        return course;
    }

    public String getPlace() {
        return place;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTime() {
        return time;
    }

    public String getCampus() {
        return campus;
    }

    public String getWeek() {
        return week;
    }

    public String getXqj() {
        return xqj;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public void setXqj(String xqj) {
        this.xqj = xqj;
    }

    public CourseTable(String course, String time, String place, String teacher, String week, String campus, String xqj){

        this.course = course;
        this.place = place;
        this.teacher = teacher;
        this.time = time;
        this.week = week;
        this.campus = campus;
        this.xqj = xqj;
    }

    public void outPrint(){
        System.out.println(getCourse());
        System.out.println(getTime());
        System.out.println(getPlace());
        System.out.println(getTeacher());
        System.out.println(getWeek());
        System.out.println(getCampus());
        System.out.println(getXqj());
    }
}

package com.xjie.myapplication;

public class ExamTable {
    String courseName,examLocation,examTime,examSeat;

    public ExamTable(String courseName,String examLocation,String examTime,String examSeat){
        this.courseName = courseName;
        this.examLocation = examLocation;
        this.examTime = examTime;
        this.examSeat = examSeat;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setExamLocation(String examLocation) {
        this.examLocation = examLocation;
    }

    public void setExamSeat(String examSeat) {
        this.examSeat = examSeat;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getExamLocation() {
        return examLocation;
    }

    public String getExamSeat() {
        return examSeat;
    }

    public String getExamTime() {
        return examTime;
    }

}

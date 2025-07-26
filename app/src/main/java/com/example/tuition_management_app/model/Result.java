package com.example.tuition_management_app.model;

public class Result {
    private long studentId;
    private long courseId;
    private long teacherId;
    private double result;
    private String studentName;

    public Result(long studentId, long courseId, long teacherId, double result, String studentName) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.result = result;
        this.studentName = studentName;
    }

    public long getStudentId() { return studentId; }
    public long getCourseId() { return courseId; }
    public long getTeacherId() { return teacherId; }
    public double getResult() { return result; }
    public String getStudentName() { return studentName; }
}

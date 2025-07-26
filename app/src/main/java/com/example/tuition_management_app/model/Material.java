package com.example.tuition_management_app.model;

public class Material {
    private long id;
    private long courseId;
    private long teacherId;
    private String title;
    private String fileUrl;
    private String uploadedAt;

    public Material(long id, long courseId, long teacherId, String title, String fileUrl, String uploadedAt) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.title = title;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
    }

    public long getId() { return id; }
    public long getCourseId() { return courseId; }
    public long getTeacherId() { return teacherId; }
    public String getTitle() { return title; }
    public String getFileUrl() { return fileUrl; }
    public String getUploadedAt() { return uploadedAt; }
}

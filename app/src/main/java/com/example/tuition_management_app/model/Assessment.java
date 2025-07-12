package com.example.tuition_management_app.model;

public class Assessment {
    private long id;
    private long courseId;
    private String title;
    private String description;
    private String dueDate;
    private String uploadUrl;

    public Assessment(long id, long courseId, String title, String description, String dueDate, String uploadUrl) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.uploadUrl = uploadUrl;
    }

    public long getId() { return id; }
    public long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public String getUploadUrl() { return uploadUrl; }
}

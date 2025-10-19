package com.versionxd.lms.backend.dto;

public class CourseEnrollmentDTO {
    private String courseId;
    private String role;

    // Constructors
    public CourseEnrollmentDTO() {
    }

    public CourseEnrollmentDTO(String courseId, String role) {
        this.courseId = courseId;
        this.role = role;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
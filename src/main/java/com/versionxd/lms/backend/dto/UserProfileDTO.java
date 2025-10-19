package com.versionxd.lms.backend.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDTO {

    private Long id;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private String email;

    private List<CourseEnrollmentDTO> enrollments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // --- ADD GETTERS AND SETTERS FOR ENROLLMENTS ---
    public List<CourseEnrollmentDTO> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<CourseEnrollmentDTO> enrollments) {
        this.enrollments = enrollments;
    }
}
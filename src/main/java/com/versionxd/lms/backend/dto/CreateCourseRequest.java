package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "Course title cannot be blank")
    private String title;

    private String description;
}

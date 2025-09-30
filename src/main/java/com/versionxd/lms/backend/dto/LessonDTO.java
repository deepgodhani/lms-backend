package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonDTO {
    private Long id;

    @NotBlank(message = "Lesson title cannot be blank")
    private String title;

    private String content;
}
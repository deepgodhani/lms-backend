package com.versionxd.lms.backend.dto;

import lombok.Data;

@Data
public class LessonDTO {
    private Long id;
    private String title;
    private String content;
    private boolean completed;
    private Long courseId; // <-- ADD THIS LINE
}
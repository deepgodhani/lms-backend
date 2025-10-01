package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizDTO {
    private Long id;
    @NotBlank(message = "Quiz title cannot be blank")
    private String title;
}
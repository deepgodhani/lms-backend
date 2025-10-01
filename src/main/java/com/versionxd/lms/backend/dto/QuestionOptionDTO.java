package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionOptionDTO {
    private Long id;

    @NotBlank(message = "Option text cannot be blank")
    private String text;

    private boolean isCorrect;
}
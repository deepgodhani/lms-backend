package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerDTO {
    @NotNull
    private Long questionId;
    @NotNull
    private Long selectedOptionId;
}
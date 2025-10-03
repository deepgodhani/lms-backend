package com.versionxd.lms.backend.dto;

import lombok.Data;

@Data
public class AnswerResultDTO {
    private Long questionId;
    private String questionText;
    private Long selectedOptionId;
    private boolean wasCorrect;
}
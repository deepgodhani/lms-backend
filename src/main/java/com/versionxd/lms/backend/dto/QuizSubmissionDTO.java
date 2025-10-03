package com.versionxd.lms.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionDTO {
    @Valid
    @NotEmpty
    private List<AnswerDTO> answers;
}
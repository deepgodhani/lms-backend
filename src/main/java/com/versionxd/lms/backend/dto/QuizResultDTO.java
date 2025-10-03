package com.versionxd.lms.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizResultDTO {
    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private double score;
    private LocalDateTime submittedAt;
    private List<AnswerResultDTO> answers;
}
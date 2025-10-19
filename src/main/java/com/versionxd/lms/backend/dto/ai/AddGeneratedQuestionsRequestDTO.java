package com.versionxd.lms.backend.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class AddGeneratedQuestionsRequestDTO {

    private Long quizId;
    private List<GeneratedQuestionDTO> questions;
}
package com.versionxd.lms.backend.dto.ai;

import com.versionxd.lms.backend.dto.QuestionOptionDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class GeneratedQuestionDTO {

    private String questionText;
    private List<QuestionOptionDTO> options;
}
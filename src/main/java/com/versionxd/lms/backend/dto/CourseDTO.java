package com.versionxd.lms.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private List<ModuleDTO> modules;
    private List<AssignmentDTO> assignments;
    private List<QuizDTO> quizzes;
}
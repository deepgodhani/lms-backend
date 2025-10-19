package com.versionxd.lms.backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModuleDTO {
    private Long id;

    @NotBlank(message = "Module title cannot be blank")
    private String title;

    private List<LessonDTO> lessons;
}
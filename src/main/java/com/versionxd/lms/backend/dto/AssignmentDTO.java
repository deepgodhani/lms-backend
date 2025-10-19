package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
    private Long id;

    @NotBlank(message = "Assignment title cannot be blank")
    private String title;

    private String instructions;


    private LocalDateTime dueDate;
}
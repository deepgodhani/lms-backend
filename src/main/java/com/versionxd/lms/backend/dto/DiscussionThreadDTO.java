package com.versionxd.lms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiscussionThreadDTO {
    private Long id;

    @NotBlank(message = "Thread title cannot be blank")
    private String title;

    @NotBlank(message = "Initial post content cannot be blank")
    private String content;
}
package com.versionxd.lms.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiscussionThreadDTO {
    private Long id;

    @NotBlank(message = "Thread title cannot be blank")
    private String title;

    @NotBlank(message = "Initial post content cannot be blank")
    private String content;

    private UserProfileDTO author;
    private LocalDateTime createdAt;
    private List<DiscussionPostDTO> posts;
}
package com.versionxd.lms.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiscussionPostDTO {
    private Long id;
    private String content;
    private UserProfileDTO author; // We can reuse the profile DTO
    private LocalDateTime createdAt;
}
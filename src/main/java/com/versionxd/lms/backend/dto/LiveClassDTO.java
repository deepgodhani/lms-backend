package com.versionxd.lms.backend.dto;

import com.versionxd.lms.backend.model.LiveClassStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LiveClassDTO {
    private Long id;
    private String title;
    private Long courseId;
    private String instructorName;
    private LiveClassStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String roomUrl;
}
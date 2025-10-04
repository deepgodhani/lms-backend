package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.DiscussionThreadDTO;
import com.versionxd.lms.backend.model.DiscussionThread;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.DiscussionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<DiscussionThread> createDiscussionThread(@PathVariable Long courseId, @Valid @RequestBody DiscussionThreadDTO dto, @AuthenticationPrincipal User currentUser) {
        DiscussionThread createdThread = discussionService.createDiscussionThread(courseId, dto, currentUser);
        return new ResponseEntity<>(createdThread, HttpStatus.CREATED);
    }
}
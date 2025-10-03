package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/lessons/{lessonId}/complete")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfLesson(#lessonId, principal.username)")
    public ResponseEntity<?> markLessonAsComplete(@PathVariable Long lessonId, @AuthenticationPrincipal User currentUser) {
        progressService.markLessonAsComplete(lessonId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Lesson marked as complete successfully."));
    }
}
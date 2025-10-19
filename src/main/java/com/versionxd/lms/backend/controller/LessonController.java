package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;



    @PutMapping("/{lessonId}/content")
    @PreAuthorize("@courseSecurityService.isInstructorForLesson(#lessonId, principal.username)")
    public ResponseEntity<LessonDTO> updateLessonContent(@PathVariable Long lessonId, @RequestBody String content) {
        LessonDTO updatedLesson = lessonService.updateLessonContent(lessonId, content);
        return ResponseEntity.ok(updatedLesson);
    }

    @GetMapping("/{lessonId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfLesson(#lessonId, principal.username)")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long lessonId) {
        LessonDTO lesson = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok(lesson);
    }

    @PutMapping("/{lessonId}")
    @PreAuthorize("@courseSecurityService.isInstructorForLesson(#lessonId, principal.username)")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonDTO lessonDTO) {
        LessonDTO updatedLesson = lessonService.updateLesson(lessonId, lessonDTO);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("@courseSecurityService.isInstructorForLesson(#lessonId, principal.username)")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
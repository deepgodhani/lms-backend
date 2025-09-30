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
@RequestMapping("/api")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @PostMapping("/modules/{moduleId}/lessons")
    @PreAuthorize("@courseSecurityService.isInstructorForModule(#moduleId, principal.username)")
    public ResponseEntity<LessonDTO> createLesson(@PathVariable Long moduleId, @Valid @RequestBody LessonDTO lessonDTO) {
        LessonDTO createdLesson = lessonService.createLesson(moduleId, lessonDTO);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    @PutMapping("/lessons/{lessonId}/content")
    @PreAuthorize("@courseSecurityService.isInstructorForLesson(#lessonId, principal.username)")
    public ResponseEntity<LessonDTO> updateLessonContent(@PathVariable Long lessonId, @RequestBody String content) {
        LessonDTO updatedLesson = lessonService.updateLessonContent(lessonId, content);
        return ResponseEntity.ok(updatedLesson);
    }
}
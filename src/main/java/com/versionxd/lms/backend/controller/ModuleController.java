package com.versionxd.lms.backend.controller;

import java.util.List;

import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.service.LessonService;
import com.versionxd.lms.backend.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {


    private final ModuleService moduleService;
    private final LessonService lessonService;

    @Autowired
    public ModuleController(ModuleService moduleService,LessonService lessonService) {
        this.moduleService = moduleService;
        this.lessonService = lessonService;
    }


    @PutMapping("/{moduleId}")
    @PreAuthorize("@courseSecurityService.isInstructorForModule(#moduleId, principal.username)")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Long moduleId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO updatedModule = moduleService.updateModule(moduleId, moduleDTO);
        return ResponseEntity.ok(updatedModule);
    }

    @DeleteMapping("/{moduleId}")
    @PreAuthorize("@courseSecurityService.isInstructorForModule(#moduleId, principal.username)")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{moduleId}/lessons")
    @PreAuthorize("@courseSecurityService.isInstructorForModule(#moduleId, principal.username)")
    public ResponseEntity<LessonDTO> createLesson(@PathVariable Long moduleId, @Valid @RequestBody LessonDTO lessonDTO) {
        LessonDTO createdLesson = lessonService.createLesson(moduleId, lessonDTO);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    @GetMapping("/{moduleId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfModule(#moduleId, principal.username)")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Long moduleId) {
        return ResponseEntity.ok(moduleService.getModuleById(moduleId));
    }

    @GetMapping("/{moduleId}/lessons")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfModule(#moduleId, principal.username)")
    public ResponseEntity<List<LessonDTO>> getLessonsForModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(moduleService.getLessonsForModule(moduleId));
    }
}
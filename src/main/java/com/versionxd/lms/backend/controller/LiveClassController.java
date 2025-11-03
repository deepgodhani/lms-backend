package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.LiveClassDTO;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.LiveClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live-classes")
public class LiveClassController {

    @Autowired
    private LiveClassService liveClassService;

    @PostMapping("/course/{courseId}")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<LiveClassDTO> scheduleClass(@PathVariable Long courseId, @RequestBody LiveClassDTO liveClassDTO, @AuthenticationPrincipal User user) {
        LiveClassDTO scheduledClass = liveClassService.scheduleClass(courseId, liveClassDTO, user);
        return ResponseEntity.ok(scheduledClass);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<List<LiveClassDTO>> getClassesForCourse(@PathVariable Long courseId) {
        List<LiveClassDTO> classes = liveClassService.getClassesForCourse(courseId);
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/{classId}/start")
    @PreAuthorize("isAuthenticated()") // Basic check, service layer has the main logic
    public ResponseEntity<LiveClassDTO> startClass(@PathVariable Long classId, @AuthenticationPrincipal User user) {
        LiveClassDTO startedClass = liveClassService.startClass(classId, user);
        return ResponseEntity.ok(startedClass);
    }

    @PostMapping("/{classId}/end")
    @PreAuthorize("isAuthenticated()") // Service layer still checks instructor
    public ResponseEntity<LiveClassDTO> endClass(@PathVariable Long classId, @AuthenticationPrincipal User user) {
        LiveClassDTO endedClass = liveClassService.endClass(classId, user);
        return ResponseEntity.ok(endedClass);
    }
}
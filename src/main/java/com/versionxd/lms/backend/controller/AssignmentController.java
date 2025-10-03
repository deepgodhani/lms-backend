package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.AssignmentDTO;
import com.versionxd.lms.backend.model.AssignmentSubmission;
import com.versionxd.lms.backend.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.versionxd.lms.backend.model.AssignmentSubmission;
import com.versionxd.lms.backend.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/courses/{courseId}/assignments")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<AssignmentDTO> createAssignment(@PathVariable Long courseId, @Valid @RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(courseId, assignmentDTO);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfAssignment(#assignmentId, principal.username)")
    public ResponseEntity<AssignmentSubmission> submitAssignment(@PathVariable Long assignmentId, @RequestBody String filePath, @AuthenticationPrincipal User currentUser) {
        AssignmentSubmission submission = assignmentService.submitAssignment(assignmentId, filePath, currentUser);
        return ResponseEntity.ok(submission);
    }

}
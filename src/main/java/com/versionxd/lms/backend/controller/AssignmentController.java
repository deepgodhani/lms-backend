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
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;


    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfAssignment(#assignmentId, principal.username)")
    public ResponseEntity<AssignmentSubmission> submitAssignment(@PathVariable Long assignmentId, @RequestBody String filePath, @AuthenticationPrincipal User currentUser) {
        AssignmentSubmission submission = assignmentService.submitAssignment(assignmentId, filePath, currentUser);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/{assignmentId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfAssignment(#assignmentId, principal.username)")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }

    // --- NEW ENDPOINT: UPDATE ASSIGNMENT ---
    @PutMapping("/{assignmentId}")
    @PreAuthorize("@courseSecurityService.isInstructorForAssignment(#assignmentId, principal.username)")
    public ResponseEntity<AssignmentDTO> updateAssignment(@PathVariable Long assignmentId, @Valid @RequestBody AssignmentDTO assignmentDTO) {
        return ResponseEntity.ok(assignmentService.updateAssignment(assignmentId, assignmentDTO));
    }

    // --- NEW ENDPOINT: DELETE ASSIGNMENT ---
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("@courseSecurityService.isInstructorForAssignment(#assignmentId, principal.username)")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }


}
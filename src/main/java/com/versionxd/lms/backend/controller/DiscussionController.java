package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.DiscussionPostDTO;
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
@RequestMapping("/api/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/courses/{courseId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<DiscussionThread> createDiscussionThread(@PathVariable Long courseId, @Valid @RequestBody DiscussionThreadDTO dto, @AuthenticationPrincipal User currentUser) {
        DiscussionThread createdThread = discussionService.createDiscussionThread(courseId, dto, currentUser);
        return new ResponseEntity<>(createdThread, HttpStatus.CREATED);
    }

    @GetMapping("/{threadId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfDiscussion(#threadId, principal.username)")
    public ResponseEntity<DiscussionThreadDTO> getDiscussionThread(@PathVariable Long threadId) {
        DiscussionThreadDTO thread = discussionService.getThreadById(threadId);
        return ResponseEntity.ok(thread);
    }

    @PostMapping("/{threadId}/posts")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfDiscussion(#threadId, principal.username)")
    public ResponseEntity<DiscussionPostDTO> addPostToThread(@PathVariable Long threadId, @RequestBody String content, @AuthenticationPrincipal User currentUser) {
        DiscussionPostDTO newPost = discussionService.addPostToThread(threadId, content, currentUser);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @PutMapping("/{threadId}")
    @PreAuthorize("@courseSecurityService.isAuthorOfThread(#threadId, principal.username)")
    public ResponseEntity<DiscussionThreadDTO> updateThread(@PathVariable Long threadId, @Valid @RequestBody DiscussionThreadDTO threadDTO) {
        return ResponseEntity.ok(discussionService.updateThread(threadId, threadDTO));
    }

    // --- NEW ENDPOINT: DELETE THREAD ---
    @DeleteMapping("/{threadId}")
    @PreAuthorize("@courseSecurityService.isAuthorOfThread(#threadId, principal.username) or @courseSecurityService.isInstructorForDiscussion(#threadId, principal.username)")
    public ResponseEntity<Void> deleteThread(@PathVariable Long threadId) {
        discussionService.deleteThread(threadId);
        return ResponseEntity.noContent().build();
    }
}
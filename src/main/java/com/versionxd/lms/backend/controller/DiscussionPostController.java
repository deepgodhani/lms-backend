package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.DiscussionPostDTO;
import com.versionxd.lms.backend.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class DiscussionPostController {

    @Autowired
    private DiscussionService discussionService;

    @PutMapping("/{postId}")
    @PreAuthorize("@courseSecurityService.isAuthorOfPost(#postId, principal.username)")
    public ResponseEntity<DiscussionPostDTO> updatePost(@PathVariable Long postId, @RequestBody String content) {
        return ResponseEntity.ok(discussionService.updatePost(postId, content));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("@courseSecurityService.isAuthorOfPost(#postId, principal.username) or @courseSecurityService.isInstructorForPost(#postId, principal.username)")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        discussionService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
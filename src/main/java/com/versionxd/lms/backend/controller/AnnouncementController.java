package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.AnnouncementDTO;
import com.versionxd.lms.backend.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@PathVariable Long courseId, @Valid @RequestBody AnnouncementDTO announcementDTO) {
        AnnouncementDTO createdAnnouncement = announcementService.createAnnouncement(courseId, announcementDTO);
        return new ResponseEntity<>(createdAnnouncement, HttpStatus.CREATED);
    }
}
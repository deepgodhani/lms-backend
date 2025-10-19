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
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;



    @PutMapping("/{announcementId}")
    @PreAuthorize("@courseSecurityService.isInstructorForAnnouncement(#announcementId, principal.username)")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(@PathVariable Long announcementId, @Valid @RequestBody AnnouncementDTO announcementDTO) {
        AnnouncementDTO updatedAnnouncement = announcementService.updateAnnouncement(announcementId, announcementDTO);
        return ResponseEntity.ok(updatedAnnouncement);
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("@courseSecurityService.isInstructorForAnnouncement(#announcementId, principal.username)")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {
        announcementService.deleteAnnouncement(announcementId);
        return ResponseEntity.noContent().build();
    }
}
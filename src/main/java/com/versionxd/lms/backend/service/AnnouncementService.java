package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.AnnouncementDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Announcement;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.repository.AnnouncementRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;

    @Transactional
    public AnnouncementDTO createAnnouncement(Long courseId, AnnouncementDTO announcementDTO) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());
        announcement.setCourse(course);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return toDTO(savedAnnouncement);
    }

    private AnnouncementDTO toDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }
}
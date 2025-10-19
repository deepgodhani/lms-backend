package com.versionxd.lms.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.AnnouncementDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Announcement;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.repository.AnnouncementRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        return announcementRepository.findAll().stream()
                                     .filter(ann -> ann.getCourse().getId().equals(courseId))
                                     .map(this::toDTO)
                                     .collect(Collectors.toList());
    }

    @Transactional
    public AnnouncementDTO updateAnnouncement(Long announcementId, AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementRepository.findById(announcementId)
                                                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return toDTO(updatedAnnouncement);
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        announcementRepository.deleteById(announcementId);
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
package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.LiveClassDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.LiveClass;
import com.versionxd.lms.backend.model.LiveClassStatus;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.LiveClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiveClassService {

    @Autowired
    private LiveClassRepository liveClassRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public LiveClassDTO scheduleClass(Long courseId, LiveClassDTO liveClassDTO, User instructor) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        LiveClass liveClass = new LiveClass();
        liveClass.setTitle(liveClassDTO.getTitle());
        liveClass.setCourse(course);
        liveClass.setInstructor(instructor);
        liveClass.setStatus(LiveClassStatus.SCHEDULED);
        liveClass.setScheduledAt(liveClassDTO.getScheduledAt());

        LiveClass savedClass = liveClassRepository.save(liveClass);
        return toDTO(savedClass);
    }

    @Transactional(readOnly = true)
    public List<LiveClassDTO> getClassesForCourse(Long courseId) {
        return liveClassRepository.findByCourseId(courseId).stream()
                                  .map(this::toDTO)
                                  .collect(Collectors.toList());
    }

    @Transactional
    public LiveClassDTO startClass(Long classId, User user) {
        LiveClass liveClass = liveClassRepository.findById(classId)
                                                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Live class not found"));

        if (!liveClass.getInstructor().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the instructor can start the class");
        }

        // We no longer create a room, just set the status.
        liveClass.setStatus(LiveClassStatus.ACTIVE);
        liveClass.setStartedAt(LocalDateTime.now());
        LiveClass updatedClass = liveClassRepository.save(liveClass);
        return toDTO(updatedClass);
    }

    @Transactional
    public LiveClassDTO endClass(Long classId, User user) {
        LiveClass liveClass = liveClassRepository.findById(classId)
                                                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Live class not found"));

        if (!liveClass.getInstructor().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the instructor can end the class");
        }

        liveClass.setStatus(LiveClassStatus.ENDED);
        liveClass.setEndedAt(LocalDateTime.now());
        LiveClass updatedClass = liveClassRepository.save(liveClass);
        return toDTO(updatedClass);
    }

    // This DTO conversion is now simpler without the roomUrl
    private LiveClassDTO toDTO(LiveClass liveClass) {
        LiveClassDTO dto = new LiveClassDTO();
        dto.setId(liveClass.getId());
        dto.setTitle(liveClass.getTitle());
        dto.setCourseId(liveClass.getCourse().getId());
        dto.setInstructorName(liveClass.getInstructor().getFirstName() + " " + liveClass.getInstructor().getLastName());
        dto.setStatus(liveClass.getStatus());
        dto.setScheduledAt(liveClass.getScheduledAt());
        dto.setStartedAt(liveClass.getStartedAt());
        dto.setEndedAt(liveClass.getEndedAt());
        // roomUrl is no longer needed
        return dto;
    }
}
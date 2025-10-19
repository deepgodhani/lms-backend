package com.versionxd.lms.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.AssignmentDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Assignment;
import com.versionxd.lms.backend.model.AssignmentSubmission;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.AssignmentRepository;
import com.versionxd.lms.backend.repository.AssignmentSubmissionRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssignmentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    @Transactional
    public AssignmentDTO createAssignment(Long courseId, AssignmentDTO assignmentDTO) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setInstructions(assignmentDTO.getInstructions());
        assignment.setDueDate(assignmentDTO.getDueDate());
        assignment.setCourse(course);

        Assignment savedAssignment = assignmentRepository.save(assignment);

        return toDTO(savedAssignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAllAssignmentsForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        return assignmentRepository.findByCourseId(courseId).stream()
                                   .map(this::toDTO)
                                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentDTO getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        return toDTO(assignment);
    }

    @Transactional
    public AssignmentDTO updateAssignment(Long assignmentId, AssignmentDTO assignmentDTO) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setInstructions(assignmentDTO.getInstructions());
        assignment.setDueDate(assignmentDTO.getDueDate());

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return toDTO(updatedAssignment);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
        assignmentRepository.deleteById(assignmentId);
    }


    @Transactional
    public AssignmentSubmission submitAssignment(Long assignmentId, String filePath, User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));


        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFilePath(filePath);

        return submissionRepository.save(submission);
    }

    private AssignmentDTO toDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setInstructions(assignment.getInstructions());
        dto.setDueDate(assignment.getDueDate());
        return dto;
    }
}
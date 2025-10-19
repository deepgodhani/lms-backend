package com.versionxd.lms.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.CourseDTO;
import com.versionxd.lms.backend.dto.CreateCourseRequest;
import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.dto.UserRoleDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.exception.UserAlreadyEnrolledException;
import com.versionxd.lms.backend.mapper.CourseMapper;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.CourseEnrollment;
import com.versionxd.lms.backend.model.Role;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.CourseEnrollmentRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.UserRepository; // <-- IMPORT THIS
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- IMPORT THIS
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;
    @Autowired private CourseMapper courseMapper;

    @Autowired
    public CourseService(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository, UserRepository userRepository) { // <-- UPDATE CONSTRUCTOR
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(CreateCourseRequest createCourseRequest, User instructor) {
        User managedInstructor = userRepository.findById(instructor.getId())
                                               .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + instructor.getId()));

        Course course = new Course();
        course.setTitle(createCourseRequest.getTitle());
        course.setDescription(createCourseRequest.getDescription());
        Course savedCourse = courseRepository.save(course);

        CourseEnrollment enrollment = new CourseEnrollment(managedInstructor, savedCourse, Role.INSTRUCTOR);
        courseEnrollmentRepository.save(enrollment);

        return savedCourse;
    }

    @Transactional
    public CourseDTO updateCourse(Long courseId, CreateCourseRequest courseUpdateRequest) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        course.setTitle(courseUpdateRequest.getTitle());
        course.setDescription(courseUpdateRequest.getDescription());

        Course updatedCourse = courseRepository.save(course);

        // We pass null for the user because we don't need completion status here
        return courseMapper.toCourseDTO(updatedCourse, null);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with ID: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public CourseEnrollment joinCourse(Long courseId, User student) {
        User managedStudent = userRepository.findById(student.getId())
                                            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + student.getId()));

        if (courseEnrollmentRepository.findByUserIdAndCourseId(managedStudent.getId(), courseId).isPresent()) {
            throw new UserAlreadyEnrolledException("User is already enrolled in this course.");
        }

        // Find the course
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        CourseEnrollment enrollment = new CourseEnrollment(managedStudent, course, Role.STUDENT);
        return courseEnrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        // No user context here, so completions will be false
        return courseRepository.findAllWithModulesAndLessons().stream()
                               .map(course -> courseMapper.toCourseDTO(course, null))
                               .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long courseId, User currentUser) { // <-- Accept User
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));
        return courseMapper.toCourseDTO(course, currentUser); // <-- Use the mapper
    }

    @Transactional(readOnly = true)
    public UserRoleDTO getUserRoleForCourse(Long courseId, User user) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                                                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not enrolled in this course"));
        return new UserRoleDTO(enrollment.getRole());
    }


}

package com.versionxd.lms.backend.service;

import java.util.List;

import com.versionxd.lms.backend.dto.CreateCourseRequest;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.exception.UserAlreadyEnrolledException;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.CourseEnrollment;
import com.versionxd.lms.backend.model.Role;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.CourseEnrollmentRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.UserRepository; // <-- IMPORT THIS
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- IMPORT THIS
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;

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
    public CourseEnrollment joinCourse(Long courseId, User student) {
        User managedStudent = userRepository.findById(student.getId())
                                            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + student.getId()));

        if (courseEnrollmentRepository.findByUser_IdAndCourse_Id(managedStudent.getId(), courseId).isPresent()) {
            throw new UserAlreadyEnrolledException("User is already enrolled in this course.");
        }

        // Find the course
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        CourseEnrollment enrollment = new CourseEnrollment(managedStudent, course, Role.STUDENT);
        return courseEnrollmentRepository.save(enrollment);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                               .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));
    }

    public boolean isUserEnrolled(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
        return courseEnrollmentRepository.findByUser_IdAndCourse_Id(user.getId(), courseId).isPresent();
    }

}

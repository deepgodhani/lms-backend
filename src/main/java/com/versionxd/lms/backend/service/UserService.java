package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.CourseDTO;
import com.versionxd.lms.backend.dto.CourseEnrollmentDTO;
import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.dto.UserProfileDTO;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.CourseEnrollment;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getMyCourses(User currentUser) {
        User userWithCourses = userRepository.findByIdWithEnrollments(currentUser.getId())
                                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Course> courses = userWithCourses.getEnrollments().stream()
                                              .map(CourseEnrollment::getCourse)
                                              .collect(Collectors.toList());

        return courses.stream()
                      .map(this::toCourseDTO)
                      .collect(Collectors.toList());
    }

    private CourseDTO toCourseDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setDescription(course.getDescription());

        List<ModuleDTO> moduleDTOs = course.getModules().stream()
                                           .map(module -> {
                                               ModuleDTO moduleDTO = new ModuleDTO();
                                               moduleDTO.setId(module.getId());
                                               moduleDTO.setTitle(module.getTitle());

                                               List<LessonDTO> lessonDTOs = module.getLessons().stream()
                                                                                  .map(lesson -> {
                                                                                      LessonDTO lessonDTO = new LessonDTO();
                                                                                      lessonDTO.setId(lesson.getId());
                                                                                      lessonDTO.setTitle(lesson.getTitle());
                                                                                      // The LOB is accessed here, while the transaction is active
                                                                                      lessonDTO.setContent(lesson.getContent());
                                                                                      return lessonDTO;
                                                                                  }).collect(Collectors.toList());

                                               moduleDTO.setLessons(lessonDTOs);
                                               return moduleDTO;
                                           }).collect(Collectors.toList());

        courseDTO.setModules(moduleDTOs);
        return courseDTO;
    }
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setFirstName(user.getFirstName());
        userProfileDTO.setLastName(user.getLastName());
        userProfileDTO.setEmail(user.getEmail());

        // --- THIS IS THE CORRECTED LOGIC ---
        List<CourseEnrollmentDTO> enrollmentDTOs = user.getEnrollments().stream()
                                                       .map(enrollment -> new CourseEnrollmentDTO(
                                                               // Convert the Long ID to a String here
                                                               String.valueOf(enrollment.getCourse().getId()),
                                                               enrollment.getRole().name()
                                                       ))
                                                       .collect(Collectors.toList());
        userProfileDTO.setEnrollments(enrollmentDTOs);
        // ------------------------------------

        return userProfileDTO;
    }
    @Transactional
    public UserProfileDTO updateUserProfile(User currentUser, UserProfileDTO profileUpdateDTO) {
        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(profileUpdateDTO.getFirstName());
        user.setLastName(profileUpdateDTO.getLastName());

        User updatedUser = userRepository.save(user);

        return getUserProfile(updatedUser);
    }
}
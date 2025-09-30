package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.CourseEnrollment;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Course> getMyCourses(User currentUser) {
        User userWithCourses = userRepository.findByIdWithEnrollments(currentUser.getId())
                                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userWithCourses.getEnrollments().stream()
                              .map(CourseEnrollment::getCourse)
                              .collect(Collectors.toList());
    }
}
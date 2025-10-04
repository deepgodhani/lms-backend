package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.UserProfileDTO;
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

    public UserProfileDTO getUserProfile(User currentUser) {
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(currentUser.getId());
        profileDTO.setFirstName(currentUser.getFirstName());
        profileDTO.setLastName(currentUser.getLastName());
        profileDTO.setEmail(currentUser.getEmail());
        return profileDTO;
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
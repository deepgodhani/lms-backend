package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.UserProfileDTO;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(@AuthenticationPrincipal User currentUser) {
        List<Course> courses = userService.getMyCourses(currentUser);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) {
        UserProfileDTO userProfile = userService.getUserProfile(currentUser);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateCurrentUserProfile(@AuthenticationPrincipal User currentUser, @Valid @RequestBody UserProfileDTO profileUpdateDTO) {
        UserProfileDTO updatedProfile = userService.updateUserProfile(currentUser, profileUpdateDTO);
        return ResponseEntity.ok(updatedProfile);
    }
}
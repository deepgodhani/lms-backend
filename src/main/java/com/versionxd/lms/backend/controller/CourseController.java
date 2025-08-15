package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.CreateCourseRequest;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest, @AuthenticationPrincipal User currentUser) {
        Course newCourse = courseService.createCourse(createCourseRequest, currentUser);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/join")
    public ResponseEntity<?> joinCourse(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        courseService.joinCourse(courseId, currentUser);
        return ResponseEntity.ok().body("Successfully joined the course.");
    }
}

package com.versionxd.lms.backend.controller;

import java.util.List;

import com.versionxd.lms.backend.dto.AnnouncementDTO;
import com.versionxd.lms.backend.dto.AssignmentDTO;
import com.versionxd.lms.backend.dto.CourseDTO;
import com.versionxd.lms.backend.dto.CreateCourseRequest;
import com.versionxd.lms.backend.dto.DiscussionThreadDTO;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.dto.UserRoleDTO;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.AnnouncementService;
import com.versionxd.lms.backend.service.AssignmentService;
import com.versionxd.lms.backend.service.CourseService;
import com.versionxd.lms.backend.service.DiscussionService;
import com.versionxd.lms.backend.service.ModuleService;
import com.versionxd.lms.backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.service.ModuleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final ModuleService moduleService;
    private final QuizService quizService;
    private final AssignmentService assignmentService;
    private final AnnouncementService announcementService;
    private final DiscussionService discussionService;


    @Autowired
    public CourseController(CourseService courseService , ModuleService moduleService , QuizService quizService,AssignmentService assignmentService , AnnouncementService announcementService,DiscussionService discussionService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.quizService = quizService;
        this.assignmentService = assignmentService;
        this.announcementService = announcementService;
        this.discussionService = discussionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest, @AuthenticationPrincipal User currentUser) {
        Course newCourse = courseService.createCourse(createCourseRequest, currentUser);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/modules")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<ModuleDTO> createModule(@PathVariable Long courseId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(courseId, moduleDTO);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }


    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<?> joinCourse(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        courseService.joinCourse(courseId, currentUser);
        return ResponseEntity.ok().body("Successfully joined the course.");
    }

    @GetMapping("/all-courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/{courseId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) { // <-- Add currentUser
        CourseDTO course = courseService.getCourseById(courseId, currentUser); // <-- Pass currentUser
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{courseId}/role")
    public ResponseEntity<UserRoleDTO> getUserRole(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        UserRoleDTO role = courseService.getUserRoleForCourse(courseId, currentUser);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CreateCourseRequest courseUpdateRequest) {
        CourseDTO updatedCourse = courseService.updateCourse(courseId, courseUpdateRequest);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build(); // Standard response for a successful deletion
    }

    @PostMapping("/{courseId}/quizzes")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<QuizDTO> createQuiz(@PathVariable Long courseId, @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(courseId, quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/assignments")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<AssignmentDTO> createAssignment(@PathVariable Long courseId, @Valid @RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(courseId, assignmentDTO);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    @GetMapping("/{courseId}/assignments")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAllAssignmentsForCourse(courseId));
    }

    @PostMapping("/{courseId}/announcements")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@PathVariable Long courseId, @Valid @RequestBody AnnouncementDTO announcementDTO) {
        AnnouncementDTO createdAnnouncement = announcementService.createAnnouncement(courseId, announcementDTO);
        return new ResponseEntity<>(createdAnnouncement, HttpStatus.CREATED);
    }

    // --- NEW ENDPOINT: GET ALL ANNOUNCEMENTS FOR A COURSE ---
    @GetMapping("/{courseId}/announcements")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncementsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsForCourse(courseId));
    }

    @GetMapping("/{courseId}/discussions")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<List<DiscussionThreadDTO>> getDiscussionThreadsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(discussionService.getThreadsForCourse(courseId));
    }

    @GetMapping("/{courseId}/quizzes")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourse(#courseId, principal.username)")
    public ResponseEntity<List<QuizDTO>> getQuizzesForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getQuizzesForCourse(courseId));
    }


}

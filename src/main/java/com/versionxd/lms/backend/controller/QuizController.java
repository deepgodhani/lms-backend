package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.dto.QuizResultDTO;
import com.versionxd.lms.backend.dto.QuizSubmissionDTO;
import com.versionxd.lms.backend.model.QuizAttempt;
import com.versionxd.lms.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.versionxd.lms.backend.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/courses/{courseId}/quizzes")
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<QuizDTO> createQuiz(@PathVariable Long courseId, @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(courseId, quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#quizId, principal.username)")
    public ResponseEntity<QuestionDTO> addQuestionToQuiz(@PathVariable Long quizId, @Valid @RequestBody QuestionDTO questionDTO) {
        QuestionDTO createdQuestion = quizService.addQuestionToQuiz(quizId, questionDTO);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfQuiz(#quizId, principal.username)")
    public ResponseEntity<QuizAttempt> submitQuiz(@PathVariable Long quizId, @Valid @RequestBody QuizSubmissionDTO submissionDTO, @AuthenticationPrincipal User currentUser) {
        QuizAttempt attempt = quizService.submitQuiz(quizId, submissionDTO, currentUser);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/quizzes/{quizId}/results")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfQuiz(#quizId, principal.username)")
    public ResponseEntity<QuizResultDTO> getQuizResults(@PathVariable Long quizId, @AuthenticationPrincipal User currentUser) {
        QuizResultDTO results = quizService.getQuizResults(quizId, currentUser);
        return ResponseEntity.ok(results);
    }
}
package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
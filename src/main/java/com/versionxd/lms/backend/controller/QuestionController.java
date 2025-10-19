package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuizService quizService; // We can reuse the QuizService for question logic

    @PutMapping("/{questionId}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuestion(#questionId, principal.username)")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionDTO questionDTO) {
        QuestionDTO updatedQuestion = quizService.updateQuestion(questionId, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuestion(#questionId, principal.username)")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuestion(#questionId, principal.username)")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long questionId) {
        return ResponseEntity.ok(quizService.getQuestionById(questionId));
    }
}
package com.versionxd.lms.backend.controller;

import java.util.List;

import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.dto.QuizResultDTO;
import com.versionxd.lms.backend.dto.QuizSubmissionDTO;
import com.versionxd.lms.backend.dto.ai.AddGeneratedQuestionsRequestDTO;
import com.versionxd.lms.backend.dto.ai.GeneratedQuestionDTO;
import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.QuizAttempt;
import com.versionxd.lms.backend.repository.LessonRepository;
import com.versionxd.lms.backend.service.AiQuizService;
import com.versionxd.lms.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.versionxd.lms.backend.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private AiQuizService aiQuizService;

    @Autowired
    private LessonRepository lessonRepository;



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

    @PostMapping("/lessons/{lessonId}/generate-questions")
    @PreAuthorize("@courseSecurityService.isInstructorForLesson(#lessonId, principal.username)")
    public ResponseEntity<List<GeneratedQuestionDTO>> generateQuestionsFromLesson(@PathVariable Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        List<GeneratedQuestionDTO> generatedQuestions = aiQuizService.generateQuestionsFromContent(lesson.getContent());
        return ResponseEntity.ok(generatedQuestions);
    }

    @PostMapping("/quizzes/add-generated-questions")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#request.quizId, principal.username)")
    public ResponseEntity<List<QuestionDTO>> addGeneratedQuestionsToQuiz(@RequestBody AddGeneratedQuestionsRequestDTO request) {
        List<QuestionDTO> addedQuestions = quizService.addGeneratedQuestionsToQuiz(request.getQuizId(), request.getQuestions());
        return ResponseEntity.ok(addedQuestions);
    }

    @PostMapping("/quizzes/{quizId}/generate-variants/{numberOfVariants}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#quizId, principal.username)")
    public ResponseEntity<String> generateQuizVariants(@PathVariable Long quizId, @PathVariable int numberOfVariants) {
        quizService.generateQuizVariants(quizId, numberOfVariants);
        return ResponseEntity.ok("Successfully generated " + numberOfVariants + " variants for the quiz.");
    }

    @GetMapping("/quizzes/{quizId}/start")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfQuiz(#quizId, principal.username)")
    public ResponseEntity<List<QuestionDTO>> startQuiz(@PathVariable Long quizId, @AuthenticationPrincipal User currentUser) {
        List<QuestionDTO> questions = quizService.getQuizQuestionsForStudent(quizId, currentUser);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("@courseSecurityService.isEnrolledInCourseOfQuiz(#quizId, principal.username)")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long quizId) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#quizId, principal.username)")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long quizId, @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO updatedQuiz = quizService.updateQuiz(quizId, quizDTO);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#quizId, principal.username)")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{quizId}/questions")
    @PreAuthorize("@courseSecurityService.isInstructorForQuiz(#quizId, principal.username)")
    public ResponseEntity<List<QuestionDTO>> getQuestionsForQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsForQuiz(quizId));
    }

}
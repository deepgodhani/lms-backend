package com.versionxd.lms.backend.repository;

import java.util.List;
import java.util.Optional;

import com.versionxd.lms.backend.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId ORDER BY qa.submittedAt DESC")
    List<QuizAttempt> findAttemptsByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);
}
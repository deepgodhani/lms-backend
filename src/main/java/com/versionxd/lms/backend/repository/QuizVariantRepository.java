package com.versionxd.lms.backend.repository;

import java.util.List;

import com.versionxd.lms.backend.model.QuizVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizVariantRepository extends JpaRepository<QuizVariant, Long> {
    List<QuizVariant> findByQuizId(Long quizId);
}
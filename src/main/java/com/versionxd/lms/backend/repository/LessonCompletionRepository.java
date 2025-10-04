package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    boolean existsByUser_IdAndLesson_Id(Long userId, Long lessonId);
}
package com.versionxd.lms.backend.repository;

import java.util.Optional;
import java.util.Set;

import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.LessonCompletion;
import com.versionxd.lms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("SELECT lc.lesson.id FROM LessonCompletion lc WHERE lc.user.id = :userId AND lc.lesson.module.course.id = :courseId")
    Set<Long> findCompletedLessonIdsByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    Optional<LessonCompletion> findByLessonAndUser(Lesson lesson, User user);
}
package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /**
     * Finds a specific enrollment record based on the user's ID and the course's ID.
     * This will be the most common query you run to check a user's permissions for a course.
     */
    Optional<CourseEnrollment> findByUser_IdAndCourse_Id(Long userId, Long courseId);
}

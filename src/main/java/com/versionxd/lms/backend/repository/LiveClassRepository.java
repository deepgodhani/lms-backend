package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.LiveClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveClassRepository extends JpaRepository<LiveClass, Long> {
    List<LiveClass> findByCourseId(Long courseId);
}
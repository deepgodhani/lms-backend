package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.DiscussionThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, Long> {
}
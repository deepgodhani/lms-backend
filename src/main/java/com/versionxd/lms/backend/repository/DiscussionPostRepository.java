package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, Long> {
}

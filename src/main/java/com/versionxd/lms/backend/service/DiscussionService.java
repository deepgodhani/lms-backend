package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.DiscussionThreadDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.*;
import com.versionxd.lms.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscussionService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DiscussionThreadRepository discussionThreadRepository;

    @Transactional
    public DiscussionThread createDiscussionThread(Long courseId, DiscussionThreadDTO dto, User currentUser) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        User author = userRepository.findById(currentUser.getId())
                                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Create the main thread
        DiscussionThread thread = new DiscussionThread();
        thread.setTitle(dto.getTitle());
        thread.setCourse(course);
        thread.setAuthor(author);

        // Create the initial post for the thread
        DiscussionPost initialPost = new DiscussionPost();
        initialPost.setContent(dto.getContent());
        initialPost.setAuthor(author);
        initialPost.setThread(thread);

        // Add the post to the thread's list of posts
        thread.getPosts().add(initialPost);

        return discussionThreadRepository.save(thread);
    }
}
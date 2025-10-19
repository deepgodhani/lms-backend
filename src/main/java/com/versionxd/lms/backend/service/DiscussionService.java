package com.versionxd.lms.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.DiscussionPostDTO;
import com.versionxd.lms.backend.dto.DiscussionThreadDTO;
import com.versionxd.lms.backend.dto.UserProfileDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.*;
import com.versionxd.lms.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DiscussionService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DiscussionThreadRepository discussionThreadRepository;
    @Autowired private DiscussionPostRepository discussionPostRepository;



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



    @Transactional
    public DiscussionPostDTO addPostToThread(Long threadId, String content, User currentUser) {
        DiscussionThread thread = discussionThreadRepository.findById(threadId)
                                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread not found"));

        User author = userRepository.findById(currentUser.getId())
                                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        DiscussionPost post = new DiscussionPost();
        post.setContent(content);
        post.setAuthor(author);
        post.setThread(thread);

        DiscussionPost savedPost = discussionPostRepository.save(post);
        return toDTO(savedPost);
    }


    @Transactional(readOnly = true)
    public DiscussionThreadDTO getThreadById(Long threadId) {
        DiscussionThread thread = discussionThreadRepository.findById(threadId)
                                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion thread not found"));
        return toDTO(thread);
    }

    @Transactional(readOnly = true)
    public List<DiscussionThreadDTO> getThreadsForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        return discussionThreadRepository.findByCourseId(courseId).stream()
                                         .map(this::toDTO)
                                         .collect(Collectors.toList());
    }

    // ... createDiscussionThread, getThreadById, addPostToThread methods ...

    // --- NEW METHOD: UPDATE THREAD ---
    @Transactional
    public DiscussionThreadDTO updateThread(Long threadId, DiscussionThreadDTO threadDTO) {
        DiscussionThread thread = discussionThreadRepository.findById(threadId)
                                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion thread not found"));

        thread.setTitle(threadDTO.getTitle());
        // Note: We typically don't allow changing the initial post content here.

        return toDTO(discussionThreadRepository.save(thread));
    }

    // --- NEW METHOD: DELETE THREAD ---
    @Transactional
    public void deleteThread(Long threadId) {
        if (!discussionThreadRepository.existsById(threadId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion thread not found");
        }
        discussionThreadRepository.deleteById(threadId);
    }

    // --- NEW METHOD: UPDATE POST ---
    @Transactional
    public DiscussionPostDTO updatePost(Long postId, String content) {
        DiscussionPost post = discussionPostRepository.findById(postId)
                                                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        post.setContent(content);
        return toDTO(discussionPostRepository.save(post));
    }

    // --- NEW METHOD: DELETE POST ---
    @Transactional
    public void deletePost(Long postId) {
        if (!discussionPostRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        discussionPostRepository.deleteById(postId);
    }

    private DiscussionThreadDTO toDTO(DiscussionThread thread) {
        DiscussionThreadDTO dto = new DiscussionThreadDTO();
        dto.setId(thread.getId());
        dto.setTitle(thread.getTitle());
        dto.setCreatedAt(thread.getCreatedAt());

        if (thread.getAuthor() != null) {
            UserProfileDTO authorDto = new UserProfileDTO();
            authorDto.setId(thread.getAuthor().getId());
            authorDto.setFirstName(thread.getAuthor().getFirstName());
            authorDto.setLastName(thread.getAuthor().getLastName());
            dto.setAuthor(authorDto);
        }

        List<DiscussionPostDTO> postDTOs = thread.getPosts().stream()
                                                 .map(this::toDTO)
                                                 .collect(Collectors.toList());
        dto.setPosts(postDTOs);

        return dto;
    }

    private DiscussionPostDTO toDTO(DiscussionPost post) {
        DiscussionPostDTO dto = new DiscussionPostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getAuthor() != null) {
            UserProfileDTO authorDto = new UserProfileDTO();
            authorDto.setId(post.getAuthor().getId());
            authorDto.setFirstName(post.getAuthor().getFirstName());
            authorDto.setLastName(post.getAuthor().getLastName());
            dto.setAuthor(authorDto);
        }
        return dto;
    }

}
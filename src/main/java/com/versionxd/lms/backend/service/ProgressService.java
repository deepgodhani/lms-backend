package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.LessonCompletion;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.LessonCompletionRepository;
import com.versionxd.lms.backend.repository.LessonRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProgressService {

    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    @Transactional
    public void markLessonAsComplete(Long lessonId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (lessonCompletionRepository.existsByUser_IdAndLesson_Id(user.getId(), lesson.getId())) {
            return;
        }

        LessonCompletion completion = new LessonCompletion(user, lesson);
        lessonCompletionRepository.save(completion);
    }
}
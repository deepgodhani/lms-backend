package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.Role;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.model.Module;
import com.versionxd.lms.backend.repository.CourseEnrollmentRepository;
import com.versionxd.lms.backend.repository.LessonRepository;
import com.versionxd.lms.backend.repository.ModuleRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.versionxd.lms.backend.model.Quiz;
import com.versionxd.lms.backend.repository.QuizRepository;

@Service("courseSecurityService")
public class CourseSecurityService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizRepository quizRepository;

    public boolean isInstructor(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return courseEnrollmentRepository.findByUser_IdAndCourse_Id(user.getId(), courseId)
                                         .map(enrollment -> enrollment.getRole() == Role.INSTRUCTOR)
                                         .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForModule(Long moduleId, String userEmail) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        Long courseId = module.getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForLesson(Long lessonId, String userEmail) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        Long courseId = lesson.getModule().getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForQuiz(Long quizId, String userEmail) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Long courseId = quiz.getCourse().getId();
        return isInstructor(courseId, userEmail);
    }
}
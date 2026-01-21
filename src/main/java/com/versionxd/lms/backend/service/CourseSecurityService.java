package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.model.*; // Using wildcard import for brevity
import com.versionxd.lms.backend.model.Module;
import com.versionxd.lms.backend.repository.*; // Using wildcard import for brevity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private DiscussionThreadRepository discussionThreadRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private DiscussionPostRepository discussionPostRepository;

    public boolean isInstructor(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                                         .map(enrollment -> enrollment.getRole() == Role.INSTRUCTOR)
                                         .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolledInCourse(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isEnrolledInCourseOfLesson(Long lessonId, String userEmail) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        Long courseId = lesson.getModule().getCourse().getId();
        return isEnrolledInCourse(courseId, userEmail);
    }

    // --- THIS IS THE MISSING METHOD ---
    @Transactional(readOnly = true)
    public boolean isEnrolledInCourseOfQuiz(Long quizId, String userEmail) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        Long courseId = quiz.getCourse().getId();
        return isEnrolledInCourse(courseId, userEmail);
    }
    // ------------------------------------

    @Transactional(readOnly = true)
    public boolean isEnrolledInCourseOfModule(Long moduleId, String userEmail) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        Long courseId = module.getCourse().getId();
        return isEnrolledInCourse(courseId, userEmail);
    }

    // ... (rest of your existing methods in the file) ...

    @Transactional(readOnly = true)
    public boolean isInstructorForModule(Long moduleId, String userEmail) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return isInstructor(module.getCourse().getId(), userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForLesson(Long lessonId, String userEmail) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        return isInstructor(lesson.getModule().getCourse().getId(), userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForQuiz(Long quizId, String userEmail) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        return isInstructor(quiz.getCourse().getId(), userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isStudentInCourseOfQuiz(Long quizId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        Long courseId = quiz.getCourse().getId();
        return courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                                         .map(enrollment -> enrollment.getRole() == Role.STUDENT)
                                         .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isStudentInCourseOfLesson(Long lessonId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        Long courseId = lesson.getModule().getCourse().getId();
        return courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                                         .map(enrollment -> enrollment.getRole() == Role.STUDENT)
                                         .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isStudentInCourseOfAssignment(Long assignmentId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        Long courseId = assignment.getCourse().getId();
        return courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                                         .map(enrollment -> enrollment.getRole() == Role.STUDENT)
                                         .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForAssignment(Long assignmentId, String userEmail) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        Long courseId = assignment.getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolledInCourseOfDiscussion(Long threadId, String userEmail) {
        DiscussionThread thread = discussionThreadRepository.findById(threadId)
                                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion thread not found"));
        Long courseId = thread.getCourse().getId();
        return isEnrolledInCourse(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForQuestion(Long questionId, String userEmail) {
        Question question = questionRepository.findById(questionId)
                                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        Long courseId = question.getQuiz().getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForAnnouncement(Long announcementId, String userEmail) {
        Announcement announcement = announcementRepository.findById(announcementId)
                                                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
        Long courseId = announcement.getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isAuthorOfThread(Long threadId, String userEmail) {
        DiscussionThread thread = discussionThreadRepository.findById(threadId)
                                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion thread not found"));
        return thread.getAuthor().getEmail().equals(userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isAuthorOfPost(Long postId, String userEmail) {
        DiscussionPost post = discussionPostRepository.findById(postId)
                                                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return post.getAuthor().getEmail().equals(userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isInstructorForPost(Long postId, String userEmail) {
        DiscussionPost post = discussionPostRepository.findById(postId)
                                                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        Long courseId = post.getThread().getCourse().getId();
        return isInstructor(courseId, userEmail);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolledInCourseOfAssignment(Long assignmentId, String userEmail) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        Long courseId = assignment.getCourse().getId();
        return isEnrolledInCourse(courseId, userEmail);
    }
}
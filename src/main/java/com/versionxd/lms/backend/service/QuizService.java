package com.versionxd.lms.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.AnswerDTO;
import com.versionxd.lms.backend.dto.AnswerResultDTO;
import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.dto.QuestionOptionDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.dto.QuizResultDTO;
import com.versionxd.lms.backend.dto.QuizSubmissionDTO;
import com.versionxd.lms.backend.dto.ai.GeneratedQuestionDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Answer;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.CourseEnrollment;
import com.versionxd.lms.backend.model.Question;
import com.versionxd.lms.backend.model.QuestionOption;
import com.versionxd.lms.backend.model.Quiz;
import com.versionxd.lms.backend.model.QuizAttempt;
import com.versionxd.lms.backend.model.QuizVariant;
import com.versionxd.lms.backend.model.Role;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.CourseEnrollmentRepository;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.QuestionOptionRepository;
import com.versionxd.lms.backend.repository.QuestionRepository;
import com.versionxd.lms.backend.repository.QuizAttemptRepository;
import com.versionxd.lms.backend.repository.QuizRepository;
import com.versionxd.lms.backend.repository.QuizVariantRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QuizAttemptRepository quizAttemptRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestionOptionRepository questionOptionRepository;
    @Autowired
    private QuizVariantRepository quizVariantRepository;
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;


    @Transactional
    public QuizDTO createQuiz(Long courseId, QuizDTO quizDTO) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setCourse(course);

        Quiz savedQuiz = quizRepository.save(quiz);

        return toDTO(savedQuiz);
    }

    @Transactional
    public QuestionDTO addQuestionToQuiz(Long quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Question question = new Question();
        question.setText(questionDTO.getText());
        question.setQuiz(quiz);

        for (QuestionOptionDTO optionDTO : questionDTO.getOptions()) {
            QuestionOption option = new QuestionOption();
            option.setText(optionDTO.getText());
            option.setCorrect(optionDTO.isCorrect());
            option.setQuestion(question);
            question.getOptions().add(option);
        }

        Question savedQuestion = questionRepository.save(question);
        return toDTO(savedQuestion);
    }

    @Transactional
    public QuizAttempt submitQuiz(Long quizId, QuizSubmissionDTO submissionDTO, User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);

        int correctAnswers = 0;

        List<Long> optionIds = submissionDTO.getAnswers().stream().map(AnswerDTO::getSelectedOptionId).collect(Collectors.toList());
        Map<Long, QuestionOption> optionsMap = questionOptionRepository.findAllById(optionIds).stream()
                                                                       .collect(Collectors.toMap(QuestionOption::getId, option -> option));

        for (AnswerDTO answerDTO : submissionDTO.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question not found: " + answerDTO.getQuestionId()));

            QuestionOption selectedOption = optionsMap.get(answerDTO.getSelectedOptionId());
            if (selectedOption == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option not found: " + answerDTO.getSelectedOptionId());
            }

            Answer answer = new Answer();
            answer.setQuizAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selectedOption);
            attempt.getAnswers().add(answer);

            if (selectedOption.isCorrect()) {
                correctAnswers++;
            }
        }

        double score = (double) correctAnswers / submissionDTO.getAnswers().size() * 100;
        attempt.setScore(score);

        return quizAttemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public QuizResultDTO getQuizResults(Long quizId, User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        List<QuizAttempt> attempts = quizAttemptRepository.findAttemptsByUserIdAndQuizId(user.getId(), quizId);

        if (attempts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz attempt found for this user.");
        }

        QuizAttempt mostRecentAttempt = attempts.get(0);

        return toResultDTO(mostRecentAttempt);
    }
    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        return toDTO(quiz);
    }

    @Transactional
    public QuizDTO updateQuiz(Long quizId, QuizDTO quizDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        quiz.setTitle(quizDTO.getTitle());
        Quiz updatedQuiz = quizRepository.save(quiz);
        return toDTO(updatedQuiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        quizRepository.deleteById(quizId);
    }

    @Transactional
    public QuestionDTO updateQuestion(Long questionId, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(questionId)
                                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        question.setText(questionDTO.getText());
        // Clear old options and add new ones to handle updates, additions, or removals
        question.getOptions().clear();
        questionRepository.save(question); // Save to persist the clear operation

        for (QuestionOptionDTO optionDTO : questionDTO.getOptions()) {
            QuestionOption option = new QuestionOption();
            option.setText(optionDTO.getText());
            option.setCorrect(optionDTO.isCorrect());
            option.setQuestion(question);
            question.getOptions().add(option);
        }

        Question updatedQuestion = questionRepository.save(question);
        return toDTO(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
        }
        questionRepository.deleteById(questionId);
    }

    private QuizDTO toDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        return dto;
    }

    private QuestionDTO toDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setOptions(question.getOptions().stream().map(opt -> {
            QuestionOptionDTO optDto = new QuestionOptionDTO();
            optDto.setId(opt.getId());
            optDto.setText(opt.getText());
            optDto.setCorrect(opt.isCorrect());
            return optDto;
        }).toList());
        return dto;
    }

    private QuizResultDTO toResultDTO(QuizAttempt attempt) {
        QuizResultDTO resultDTO = new QuizResultDTO();
        resultDTO.setAttemptId(attempt.getId());
        resultDTO.setQuizId(attempt.getQuiz().getId());
        resultDTO.setQuizTitle(attempt.getQuiz().getTitle());
        resultDTO.setScore(attempt.getScore());
        resultDTO.setSubmittedAt(attempt.getSubmittedAt());

        List<AnswerResultDTO> answerResults = attempt.getAnswers().stream().map(answer -> {
            AnswerResultDTO answerDTO = new AnswerResultDTO();
            answerDTO.setQuestionId(answer.getQuestion().getId());
            answerDTO.setQuestionText(answer.getQuestion().getText());
            answerDTO.setSelectedOptionId(answer.getSelectedOption().getId());
            answerDTO.setWasCorrect(answer.getSelectedOption().isCorrect());
            return answerDTO;
        }).collect(Collectors.toList());

        resultDTO.setAnswers(answerResults);
        return resultDTO;
    }

    @Transactional
    public List<QuestionDTO> addGeneratedQuestionsToQuiz(Long quizId, List<GeneratedQuestionDTO> generatedQuestions) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<Question> questionsToAdd = new ArrayList<>();

        for (GeneratedQuestionDTO generatedQuestion : generatedQuestions) {
            Question question = new Question();
            question.setText(generatedQuestion.getQuestionText());
            question.setQuiz(quiz);

            for (QuestionOptionDTO optionDTO : generatedQuestion.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setText(optionDTO.getText());
                option.setCorrect(optionDTO.isCorrect());
                option.setQuestion(question);
                question.getOptions().add(option);
            }
            questionsToAdd.add(question);
        }

        List<Question> savedQuestions = questionRepository.saveAll(questionsToAdd);
        return savedQuestions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void generateQuizVariants(Long quizId, int numberOfVariants) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<Question> allQuestions = new ArrayList<>(quiz.getQuestions());
        Collections.shuffle(allQuestions);

        int questionsPerVariant = allQuestions.size() / numberOfVariants;

        for (int i = 0; i < numberOfVariants; i++) {
            QuizVariant variant = new QuizVariant();
            variant.setQuiz(quiz);
            variant.setName("Set " + (char)('A' + i));

            int start = i * questionsPerVariant;
            int end = start + questionsPerVariant;

            if (i == numberOfVariants - 1) {
                // Add any remaining questions to the last variant
                end = allQuestions.size();
            }

            List<Question> variantQuestions = allQuestions.subList(start, end);
            for (Question q : variantQuestions) {
                // you would use a join table (`QuizVariantQuestion`)
                // to associate questions with variants.
            }
            quizVariantRepository.save(variant);
        }
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuizQuestionsForStudent(Long quizId, User currentUser) {
        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuizVariant> variants = new ArrayList<>(quiz.getVariants());
        if (variants.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This quiz has no variants generated.");
        }

        // Get all student enrollments for the course and sort them to create a consistent order
        List<CourseEnrollment> studentEnrollments = quiz.getCourse().getEnrollments().stream()
                                                        .filter(e -> e.getRole() == Role.STUDENT)
                                                        .sorted(Comparator.comparing(e -> e.getUser().getId()))
                                                        .collect(Collectors.toList());

        // Find the position (index) of the current student in the sorted list
        int studentIndex = -1;
        for (int i = 0; i < studentEnrollments.size(); i++) {
            if (studentEnrollments.get(i).getUser().getId().equals(currentUser.getId())) {
                studentIndex = i;
                break;
            }
        }

        if (studentIndex == -1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not enrolled as a student in this course.");
        }

        // Use the modulus operator to assign a variant. This is the core of your unique logic!
        // studentIndex 0 gets variant 0, index 1 gets variant 1, index 2 gets variant 0 (if 2 variants), etc.
        QuizVariant assignedVariant = variants.get(studentIndex % variants.size());

        // Return the questions for the assigned variant
        return assignedVariant.getQuestions().stream()
                              .map(this::toDTO) // We can reuse the existing toDTO(Question) method
                              .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        // This assumes a findByCourseId method in QuizRepository
        return quizRepository.findAll().stream()
                             .filter(quiz -> quiz.getCourse().getId().equals(courseId))
                             .map(this::toDTO)
                             .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        return toDTO(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsForQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        return questionRepository.findAll().stream()
                                 .filter(q -> q.getQuiz().getId().equals(quizId))
                                 .map(this::toDTO)
                                 .collect(Collectors.toList());
    }
}
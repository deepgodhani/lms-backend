package com.versionxd.lms.backend.service;

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
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Answer;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.Question;
import com.versionxd.lms.backend.model.QuestionOption;
import com.versionxd.lms.backend.model.Quiz;
import com.versionxd.lms.backend.model.QuizAttempt;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.QuestionOptionRepository;
import com.versionxd.lms.backend.repository.QuestionRepository;
import com.versionxd.lms.backend.repository.QuizAttemptRepository;
import com.versionxd.lms.backend.repository.QuizRepository;
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
}
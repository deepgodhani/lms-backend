package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.QuestionDTO;
import com.versionxd.lms.backend.dto.QuestionOptionDTO;
import com.versionxd.lms.backend.dto.QuizDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.Question;
import com.versionxd.lms.backend.model.QuestionOption;
import com.versionxd.lms.backend.model.Quiz;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.QuestionRepository;
import com.versionxd.lms.backend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;

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

        // Create the question
        Question question = new Question();
        question.setText(questionDTO.getText());
        question.setQuiz(quiz);

        // Create and associate the options
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
}
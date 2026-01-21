package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.Module;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.LessonCompletionRepository;
import com.versionxd.lms.backend.repository.LessonRepository;
import com.versionxd.lms.backend.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LessonService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    @Transactional
    public LessonDTO createLesson(Long moduleId, LessonDTO lessonDTO) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDTO.getTitle());
        lesson.setModule(module);
        lesson.setContent(lessonDTO.getContent());

        Lesson savedLesson = lessonRepository.save(lesson);

        return toDTO(savedLesson);
    }

    @Transactional
    public LessonDTO updateLessonContent(Long lessonId, String content) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        lesson.setContent(content);

        return toDTO(lesson);
    }
    @Transactional(readOnly = true)
    public LessonDTO getLessonById(Long lessonId, User user) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        boolean isCompleted = lessonCompletionRepository.findByLessonAndUser(lesson, user).isPresent();

        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setTitle(lesson.getTitle());
        lessonDTO.setContent(lesson.getContent());
        lessonDTO.setCompleted(isCompleted);

        lessonDTO.setCourseId(lesson.getModule().getCourse().getId());

        return lessonDTO;
    }

    @Transactional
    public LessonDTO updateLesson(Long lessonId, LessonDTO lessonDTO) {
        Lesson lesson = lessonRepository.findById(lessonId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        lesson.setTitle(lessonDTO.getTitle());
        lesson.setContent(lessonDTO.getContent());

        Lesson updatedLesson = lessonRepository.save(lesson);
        return toDTO(updatedLesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");
        }
        lessonRepository.deleteById(lessonId);
    }



    private LessonDTO toDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContent(lesson.getContent());
        return dto;
    }
}
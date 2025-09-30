package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.Module;
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

    private LessonDTO toDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContent(lesson.getContent());
        return dto;
    }
}
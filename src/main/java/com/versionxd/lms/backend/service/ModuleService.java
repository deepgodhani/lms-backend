package com.versionxd.lms.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.versionxd.lms.backend.dto.LessonDTO;
import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.Lesson;
import com.versionxd.lms.backend.model.Module;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.LessonRepository;
import com.versionxd.lms.backend.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ModuleService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    @Autowired
    public ModuleService(CourseRepository courseRepository, ModuleRepository moduleRepository, LessonRepository lessonRepository){
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public ModuleDTO createModule(Long courseId, ModuleDTO moduleDTO) {
        Course course = courseRepository.findById(courseId)
                                        .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        Module module = new Module();
        module.setTitle(moduleDTO.getTitle());
        module.setCourse(course);

        Module savedModule = moduleRepository.save(module);

        ModuleDTO responseDTO = new ModuleDTO();
        responseDTO.setId(savedModule.getId());
        responseDTO.setTitle(savedModule.getTitle());

        return responseDTO;
    }

    @Transactional
    public ModuleDTO updateModule(Long moduleId, ModuleDTO moduleDTO) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        module.setTitle(moduleDTO.getTitle());
        Module updatedModule = moduleRepository.save(module);
        return toDTO(updatedModule);
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found");
        }
        moduleRepository.deleteById(moduleId);
    }
    @Transactional(readOnly = true)
    public ModuleDTO getModuleById(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return toDTO(module);
    }

    // --- NEW METHOD: GET ALL LESSONS FOR A MODULE ---
    @Transactional(readOnly = true)
    public List<LessonDTO> getLessonsForModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found");
        }
        return lessonRepository.findAll().stream()
                               .filter(lesson -> lesson.getModule().getId().equals(moduleId))
                               .map(this::toLessonDTO)
                               .collect(Collectors.toList());
    }


    private ModuleDTO toDTO(Module module) {
        ModuleDTO responseDTO = new ModuleDTO();
        responseDTO.setId(module.getId());
        responseDTO.setTitle(module.getTitle());
        return responseDTO;
    }

    private LessonDTO toLessonDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContent(lesson.getContent());
        return dto;
    }
}
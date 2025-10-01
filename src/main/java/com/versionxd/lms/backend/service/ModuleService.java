package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.exception.CourseNotFoundException;
import com.versionxd.lms.backend.model.Course;
import com.versionxd.lms.backend.model.Module;
import com.versionxd.lms.backend.repository.CourseRepository;
import com.versionxd.lms.backend.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModuleService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    @Autowired
    public ModuleService(CourseRepository courseRepository, ModuleRepository moduleRepository) {
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
}
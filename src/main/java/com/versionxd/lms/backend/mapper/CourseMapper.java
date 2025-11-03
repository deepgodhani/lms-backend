// lms-backend/src/main/java/com/versionxd/lms/backend/mapper/CourseMapper.java
package com.versionxd.lms.backend.mapper;

import com.versionxd.lms.backend.dto.*; // Import all DTOs
import com.versionxd.lms.backend.model.*; // Import all models
import com.versionxd.lms.backend.repository.LessonCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    public CourseDTO toCourseDTO(Course course, User user) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setDescription(course.getDescription());

        Set<Long> completedLessonIds = user != null ?
                lessonCompletionRepository.findCompletedLessonIdsByUserIdAndCourseId(user.getId(), course.getId()) :
                Collections.emptySet();

        List<ModuleDTO> moduleDTOs = course.getModules().stream()
                                           .map(module -> {
                                               ModuleDTO moduleDTO = new ModuleDTO();
                                               moduleDTO.setId(module.getId());
                                               moduleDTO.setTitle(module.getTitle());

                                               List<LessonDTO> lessonDTOs = module.getLessons().stream()
                                                                                  .map(lesson -> {
                                                                                      LessonDTO lessonDTO = new LessonDTO();
                                                                                      lessonDTO.setId(lesson.getId());
                                                                                      lessonDTO.setTitle(lesson.getTitle());
                                                                                      lessonDTO.setContent(lesson.getContent());
                                                                                      lessonDTO.setCompleted(completedLessonIds.contains(lesson.getId()));
                                                                                      return lessonDTO;
                                                                                  }).collect(Collectors.toList());

                                               moduleDTO.setLessons(lessonDTOs);
                                               return moduleDTO;
                                           }).collect(Collectors.toList());

        courseDTO.setModules(moduleDTOs);

        if (course.getAssignments() != null) {
            List<AssignmentDTO> assignmentDTOs = course.getAssignments().stream()
                                                       .map(this::toAssignmentDTO)
                                                       .collect(Collectors.toList());
            courseDTO.setAssignments(assignmentDTOs);
        } else {
            courseDTO.setAssignments(Collections.emptyList());
        }

        if (course.getQuizzes() != null) {
            List<QuizDTO> quizDTOs = course.getQuizzes().stream()
                                           .map(this::toQuizDTO)
                                           .collect(Collectors.toList());
            courseDTO.setQuizzes(quizDTOs);
        } else {
            courseDTO.setQuizzes(Collections.emptyList());
        }

        // --- NEW MAPPING FOR LIVE CLASSES ---
        if (course.getLiveClasses() != null) {
            List<LiveClassDTO> liveClassDTOs = course.getLiveClasses().stream()
                                                     .map(this::toLiveClassDTO)
                                                     .collect(Collectors.toList());
            courseDTO.setLiveClasses(liveClassDTOs);
        } else {
            courseDTO.setLiveClasses(Collections.emptyList());
        }
        // ------------------------------------

        return courseDTO;
    }

    private AssignmentDTO toAssignmentDTO(Assignment assignment) {
        if (assignment == null) { return null; }
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setInstructions(assignment.getInstructions());
        dto.setDueDate(assignment.getDueDate());
        return dto;
    }

    private QuizDTO toQuizDTO(Quiz quiz) {
        if (quiz == null) { return null; }
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        return dto;
    }

    // --- NEW HELPER METHOD FOR LIVE CLASS ---
    private LiveClassDTO toLiveClassDTO(LiveClass liveClass) {
        if (liveClass == null) { return null; }
        LiveClassDTO dto = new LiveClassDTO();
        dto.setId(liveClass.getId());
        dto.setTitle(liveClass.getTitle());
        if (liveClass.getCourse() != null) {
            dto.setCourseId(liveClass.getCourse().getId());
        }
        if (liveClass.getInstructor() != null) {
            dto.setInstructorName(liveClass.getInstructor().getFirstName() + " " + liveClass.getInstructor().getLastName());
        }
        dto.setStatus(liveClass.getStatus());
        dto.setScheduledAt(liveClass.getScheduledAt());
        dto.setStartedAt(liveClass.getStartedAt());
        dto.setEndedAt(liveClass.getEndedAt());
        dto.setRoomUrl(liveClass.getRoomUrl());
        return dto;
    }
}
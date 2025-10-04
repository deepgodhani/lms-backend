package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.ModuleDTO;
import com.versionxd.lms.backend.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/modules")
public class ModuleController {


    private final ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping
    @PreAuthorize("@courseSecurityService.isInstructor(#courseId, principal.username)")
    public ResponseEntity<ModuleDTO> createModule(@PathVariable Long courseId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(courseId, moduleDTO);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }
}
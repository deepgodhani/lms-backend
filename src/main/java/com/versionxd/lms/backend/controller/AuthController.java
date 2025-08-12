package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.RegisterRequest;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base URL for all endpoints in this controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = authService.register(registerRequest);
            // For now, we just return a success message.
            // Later, we can return a proper response DTO.
            return ResponseEntity.ok("User registered successfully! ID: " + registeredUser.getId());
        } catch (IllegalArgumentException e) {
            // This catches the "email already in use" error from our service.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

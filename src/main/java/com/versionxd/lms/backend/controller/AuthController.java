package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.dto.AuthResponse;
import com.versionxd.lms.backend.dto.LoginRequest;
import com.versionxd.lms.backend.dto.RegisterRequest;
import com.versionxd.lms.backend.dto.UserProfileDTO;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.service.AuthService;
import com.versionxd.lms.backend.service.JwtService;
import com.versionxd.lms.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController

@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService,UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authService.login(loginRequest);
        User userDetails = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        UserProfileDTO userProfile = userService.getUserProfile(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, userProfile));
    }
}

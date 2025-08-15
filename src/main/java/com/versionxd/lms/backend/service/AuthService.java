package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.AuthResponse;
import com.versionxd.lms.backend.dto.LoginRequest;
import com.versionxd.lms.backend.dto.RegisterRequest;
import com.versionxd.lms.backend.exception.UserAlreadyExistsException;
import com.versionxd.lms.backend.model.SystemRole;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.SystemRoleRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.versionxd.lms.backend.model.SystemRole;
import com.versionxd.lms.backend.model.SystemRoleName;
import com.versionxd.lms.backend.repository.SystemRoleRepository;

import java.util.HashSet;
import java.util.Collections;


@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SystemRoleRepository systemRoleRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       SystemRoleRepository systemRoleRepository) { // 2. Add to constructor
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.systemRoleRepository = systemRoleRepository; // 3. Initialize it
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Find the default role and assign it to the new user
        SystemRole userRole = systemRoleRepository.findByName(SystemRoleName.ROLE_USER)
                                                  .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
        user.setSystemRoles(new HashSet<>(Collections.singletonList(userRole)));

        userRepository.save(user);

        return new AuthResponse("User registered successfully!");
    }
    public Authentication login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}

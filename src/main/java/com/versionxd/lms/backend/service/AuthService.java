package com.versionxd.lms.backend.service;

import com.versionxd.lms.backend.dto.AuthResponse;
import com.versionxd.lms.backend.dto.LoginRequest;
import com.versionxd.lms.backend.dto.RegisterRequest;
import com.versionxd.lms.backend.dto.UserProfileDTO;
import com.versionxd.lms.backend.exception.UserAlreadyExistsException;
import com.versionxd.lms.backend.model.SystemRole;
import com.versionxd.lms.backend.model.SystemRoleName;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.SystemRoleRepository;
import com.versionxd.lms.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.versionxd.lms.backend.dto.UserProfileDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SystemRoleRepository systemRoleRepository;
    private final JwtService jwtService;
    private final UserService userService;
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       SystemRoleRepository systemRoleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.systemRoleRepository = systemRoleRepository;
        this.userService = userService;
    }

    @Transactional // <-- Add Transactional annotation
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email address already in use.");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<SystemRole> roles = new HashSet<>();
        SystemRole userRole = systemRoleRepository.findByName(SystemRoleName.ROLE_USER)
                                                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setSystemRoles(roles);

        User savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser);

        UserProfileDTO userProfileDTO = userService.getUserProfile(savedUser);

        return new AuthResponse(jwt, userProfileDTO);
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
package com.versionxd.lms.backend.service;


import com.versionxd.lms.backend.dto.RegisterRequest;
import com.versionxd.lms.backend.model.User;
import com.versionxd.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest registerRequest) {
        // Step 1: Check if the email already exists in the database.
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            // If it exists, throw an exception.
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Step 2: If the email is new, create a new User object.
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());

        // Step 3: Hash the password before saving it. NEVER save plain text passwords.
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Step 4: Save the new user to the database.
        User savedUser = userRepository.save(user);

        return savedUser;
    }
}

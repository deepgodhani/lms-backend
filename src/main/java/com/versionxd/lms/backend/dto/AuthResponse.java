package com.versionxd.lms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Creates a constructor with all fields
public class AuthResponse {
    private String token;
}
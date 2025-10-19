package com.versionxd.lms.backend.dto;

import com.versionxd.lms.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleDTO {
    private Role role;
}
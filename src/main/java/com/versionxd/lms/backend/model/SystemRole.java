package com.versionxd.lms.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "system_roles")
@Data
public class SystemRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private SystemRoleName name;

    // @ManyToMany(mappedBy = "systemRoles")
    // private Set<User> users = new HashSet<>();
}
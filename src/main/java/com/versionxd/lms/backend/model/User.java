package com.versionxd.lms.backend.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;

@Data // Lombok: Adds getters, setters, toString(), etc. automatically
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // This will store the hashed password

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemRole systemRole;

    // We will add this later, it's the key to our contextual roles
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseEnrollment> enrollments = new HashSet<>();

    // We will add relationships to other tables like courses later.
}
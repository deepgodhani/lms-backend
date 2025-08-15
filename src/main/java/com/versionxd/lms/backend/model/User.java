package com.versionxd.lms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails { // <-- IMPLEMENTS UserDetails

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
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<CourseEnrollment> enrollments = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_system_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<SystemRole> systemRoles = new HashSet<>();


    // --- UserDetails Methods Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This now correctly provides authorities like "ROLE_USER" to Spring Security
        return systemRoles.stream()
                          .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                          .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        // Our "username" is the email address.
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isEnabled() {
        return true; // Or add logic for this
    }
}

package com.versionxd.lms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "course_enrollments", uniqueConstraints = {
        // This ensures a user can only be enrolled in a course once.
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Instant enrolledAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = Instant.now();
    }

    public CourseEnrollment(User user, Course course, Role role) {
        this.user = user;
        this.course = course;
        this.role = role;
    }
}

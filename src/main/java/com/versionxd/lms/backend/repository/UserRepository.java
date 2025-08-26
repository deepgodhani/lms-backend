package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // --- UPDATED QUERY ---
    // This query now fetches BOTH the systemRoles and the enrollments collections.
    // This creates a fully initialized User object for the security context,
    // preventing any LazyInitializationExceptions down the line.
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.systemRoles LEFT JOIN FETCH u.enrollments WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}

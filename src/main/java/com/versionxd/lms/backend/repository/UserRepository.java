package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email address
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // This query fetches the user and explicitly joins and loads the systemRoles collection
    // to prevent LazyInitializationException in the security context.
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.systemRoles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}

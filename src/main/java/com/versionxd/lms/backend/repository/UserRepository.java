package com.versionxd.lms.backend.repository;

import com.versionxd.lms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will automatically create a query for this method
    // It will look for a user by their email address.
    Optional<User> findByEmail(String email);

    // This method will check if a user with the given email already exists.
    Boolean existsByEmail(String email);
}
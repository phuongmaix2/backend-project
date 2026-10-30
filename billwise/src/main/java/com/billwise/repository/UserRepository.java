
package com.billwise.repository;

import com.billwise.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


//Handles database operations for User entities.
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by username or email.
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Checks if a username or email already exists.
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

package com.fintrack.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Database access for User.
 *
 * By extending JpaRepository<User, Long> we inherit save(), findById(),
 * findAll(), deleteById(), etc. — fully implemented by Spring at runtime.
 * We only declare the extra queries we need; Spring writes the SQL from the
 * method name.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** SELECT * FROM users WHERE email = ?  (used at login). */
    Optional<User> findByEmail(String email);

    /** SELECT COUNT(*) > 0 ... WHERE email = ?  (used to block duplicate signups). */
    boolean existsByEmail(String email);
}

package com.fintrack.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.OffsetDateTime;

/**
 * Mirrors the "users" table. One User object == one row.
 *
 * Lombok annotations generate the boilerplate for us:
 *   @Getter / @Setter   -> getEmail(), setEmail(...), etc.
 *   @NoArgsConstructor   -> an empty constructor, which JPA requires.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    /** Primary key. The database generates the value (IDENTITY column). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login identifier. Unique: no two users may share an email. */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The hashed password. We NEVER store the raw password — only a one-way
     * scrambled version (set in step 3d). The column name is "password_hash".
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * When the account was created. The database default fills this in, and
     * @Generated tells Hibernate to read the value back after INSERT so the
     * in-memory object isn't left with null.
     */
    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** Convenience constructor used when registering a new user. */
    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}

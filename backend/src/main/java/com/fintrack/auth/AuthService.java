package com.fintrack.auth;

import com.fintrack.auth.dto.AuthDtos.AuthRequest;
import com.fintrack.auth.dto.AuthDtos.AuthResponse;
import com.fintrack.security.JwtService;
import com.fintrack.user.User;
import com.fintrack.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Business logic for authentication: register a new user (hash the password)
 * and log in (verify the password, issue a JWT).
 */
@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Create a new account. Fails (409) if the email is already taken. */
    @Transactional
    public AuthResponse register(AuthRequest req) {
        String email = req.email().toLowerCase().trim();
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        // Hash the password with BCrypt — the raw password is never stored.
        String hash = passwordEncoder.encode(req.password());
        User saved = users.save(new User(email, hash));
        String token = jwtService.generateToken(saved.getId());
        return new AuthResponse(token, saved.getEmail());
    }

    /** Verify credentials and return a fresh JWT. 401 on any mismatch. */
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest req) {
        String email = req.email().toLowerCase().trim();
        User user = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // Compare the attempted password against the stored hash.
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token, user.getEmail());
    }
}

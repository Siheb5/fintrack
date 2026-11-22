package com.fintrack.auth;

import com.fintrack.auth.dto.AuthDtos.AuthRequest;
import com.fintrack.auth.dto.AuthDtos.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Public authentication endpoints. No token required to reach these — they are
 * how you GET a token in the first place.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register -> 201 Created, returns a JWT. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest req) {
        return authService.register(req);
    }

    /** POST /api/auth/login -> 200 OK, returns a JWT. */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        return authService.login(req);
    }
}

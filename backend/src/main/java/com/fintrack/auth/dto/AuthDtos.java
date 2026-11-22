package com.fintrack.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The request/response shapes for authentication, grouped in one file.
 */
public final class AuthDtos {

    private AuthDtos() {}

    /** Body for register and login: email + password. */
    public record AuthRequest(
            @NotBlank @Email(message = "must be a valid email") String email,
            @NotBlank @Size(min = 8, message = "password must be at least 8 characters") String password
    ) {}

    /** What we return after a successful register/login: the JWT token. */
    public record AuthResponse(String token, String email) {}
}

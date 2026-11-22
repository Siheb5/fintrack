package com.fintrack.config;

import com.fintrack.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Real security configuration (replaces the temporary "allow all" from 3a).
 *
 * Rules:
 *   - /api/auth/** (register, login) and /api/health are PUBLIC.
 *   - everything else requires a valid JWT.
 *   - the session is STATELESS: the server keeps no session; identity comes
 *     from the token on each request.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Stateless JSON API: no CSRF tokens, no server-side sessions.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**", "/api/health").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated())
            // Return 401 (not the default 403) when a request lacks a valid token.
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    (request, response, authEx) ->
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                                    "Unauthorized")))
            // Allow the H2 console frames during local dev.
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // Run our JWT filter before Spring's username/password filter.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** BCrypt: the industry-standard password hashing algorithm. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

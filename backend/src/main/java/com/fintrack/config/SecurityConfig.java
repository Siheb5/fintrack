package com.fintrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY security configuration for step 3a.
 *
 * Spring Security, just by being on the classpath, locks down every endpoint
 * by default. We have not built login yet, so for now we allow all requests so
 * the health endpoint works. In step 3d we REPLACE this with real JWT-based
 * rules (public /api/auth/**, everything else requires a valid token).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF: we are a stateless JSON API, not a browser-form app.
            .csrf(csrf -> csrf.disable())
            // TEMPORARY: permit every request. Tightened in step 3d.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // Allow the H2 console's frames to render during local dev.
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}

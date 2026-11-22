package com.fintrack.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A minimal endpoint used to confirm the backend is up and responding.
 *
 * @RestController   = this class handles web requests and returns data (JSON),
 *                     not HTML pages.
 * @GetMapping("/api/health") = respond to HTTP GET requests at that path.
 *
 * Returning a Map is automatically converted to JSON: {"status":"UP", ...}
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "fintrack-backend"
        );
    }
}

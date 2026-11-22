package com.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The entry point of the whole backend.
 *
 * The @SpringBootApplication annotation tells Spring to:
 *   1. auto-configure itself based on the libraries on the classpath,
 *   2. scan this package (com.fintrack) and below for our components,
 *   3. start the embedded web server.
 *
 * Running main() starts the entire application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan   // picks up @ConfigurationProperties classes like JwtProperties
public class FintrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintrackApplication.class, args);
    }
}

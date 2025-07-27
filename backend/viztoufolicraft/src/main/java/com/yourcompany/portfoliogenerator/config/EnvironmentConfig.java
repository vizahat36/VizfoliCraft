package com.yourcompany.portfoliogenerator.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration class to load environment variables from .env file
 * This ensures that environment variables are loaded before Spring Boot
 * processes the application.properties file
 */
@Configuration
public class EnvironmentConfig {

    private final Environment environment;

    public EnvironmentConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Load .env file if it exists
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // Set system properties from .env file
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Only set if not already set as system property or environment variable
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });

            System.out.println("✅ Environment variables loaded successfully from .env file");
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not load .env file (this is normal in production): " + e.getMessage());
        }
    }
}

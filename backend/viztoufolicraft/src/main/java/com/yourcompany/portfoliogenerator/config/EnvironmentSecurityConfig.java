package com.yourcompany.portfoliogenerator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class EnvironmentSecurityConfig {

    @Value("${JWT_SECRET:}")
    private String jwtSecret;

    @Value("${MONGODB_URI:}")
    private String mongodbUri;

    @Value("${GITHUB_TOKEN:}")
    private String githubToken;

    @Value("${LINKEDIN_CLIENT_SECRET:}")
    private String linkedinClientSecret;

    @PostConstruct
    public void validateSecurityConfiguration() {
        if (jwtSecret.isEmpty()) {
            throw new IllegalStateException(
                "JWT_SECRET environment variable is required but not set. " +
                "Please check your .env file or environment configuration."
            );
        }

        if (mongodbUri.isEmpty()) {
            throw new IllegalStateException(
                "MONGODB_URI environment variable is required but not set. " +
                "Please check your .env file or environment configuration."
            );
        }

        // Log security status (without revealing actual values)
        System.out.println("🔐 Security Configuration Status:");
        System.out.println("  ✅ JWT Secret: " + (jwtSecret.length() > 10 ? "Configured" : "Too Short"));
        System.out.println("  ✅ MongoDB URI: " + (mongodbUri.contains("mongodb") ? "Configured" : "Missing"));
        System.out.println("  " + (githubToken.isEmpty() ? "⚠️" : "✅") + " GitHub Token: " + 
                         (githubToken.isEmpty() ? "Not Configured" : "Configured"));
        System.out.println("  " + (linkedinClientSecret.isEmpty() ? "⚠️" : "✅") + " LinkedIn Secret: " + 
                         (linkedinClientSecret.isEmpty() ? "Not Configured" : "Configured"));
    }

    // Utility methods to check if integrations are available
    public boolean isGitHubIntegrationAvailable() {
        return !githubToken.isEmpty();
    }

    public boolean isLinkedInIntegrationAvailable() {
        return !linkedinClientSecret.isEmpty();
    }
}

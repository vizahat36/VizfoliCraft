package com.yourcompany.portfoliogenerator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.integration")
@Getter
@Setter
public class IntegrationConfig {
    
    private GitHub github = new GitHub();
    private LinkedIn linkedin = new LinkedIn();
    
    @Getter
    @Setter
    public static class GitHub {
        private String apiUrl = "https://api.github.com";
        private String token; // GitHub Personal Access Token (optional, for higher rate limits)
        private int timeout = 5000; // 5 seconds
        private int retryAttempts = 3;
        private String userAgent = "Portfolio-Generator/1.0";
    }
    
    @Getter
    @Setter
    public static class LinkedIn {
        private String apiUrl = "https://api.linkedin.com/v2";
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private int timeout = 5000; // 5 seconds
        private int retryAttempts = 3;
        private String[] scopes = {"r_liteprofile", "r_emailaddress"};
    }
}

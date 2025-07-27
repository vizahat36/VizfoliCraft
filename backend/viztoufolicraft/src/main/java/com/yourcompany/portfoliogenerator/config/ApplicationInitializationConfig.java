package com.yourcompany.portfoliogenerator.config;

import com.yourcompany.portfoliogenerator.service.EnhancedTemplateService;
import com.yourcompany.portfoliogenerator.service.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitializationConfig {
    
    private final EnhancedTemplateService templateService;
    private final GamificationService gamificationService;
    
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            try {
                log.info("Initializing application data...");
                
                // Initialize default templates
                templateService.initializeDefaultTemplates();
                
                // Initialize default badges
                gamificationService.initializeDefaultBadges();
                
                log.info("Application data initialization completed successfully");
            } catch (Exception e) {
                log.error("Error during application data initialization: {}", e.getMessage(), e);
            }
        };
    }
}

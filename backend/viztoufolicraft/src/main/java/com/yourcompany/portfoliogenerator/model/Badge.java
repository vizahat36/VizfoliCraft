package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private String description;
    
    private BadgeType badgeType;
    
    private BadgeCategory category;
    
    private Integer pointsRequired;
    
    private String iconUrl;
    
    private String colorCode;
    
    private Boolean isActive = true;
    
    private RarityLevel rarityLevel;
    
    private LocalDateTime createdAt;
    
    // Lifecycle method for MongoDB
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    public enum BadgeType {
        ACHIEVEMENT,
        MILESTONE,
        STREAK,
        SPECIAL_EVENT,
        SKILL_BASED,
        SOCIAL,
        ACTIVITY
    }
    
    public enum BadgeCategory {
        PROFILE_COMPLETION,
        PROJECT_CREATION,
        SOCIAL_INTEGRATION,
        RESUME_GENERATION,
        COMMUNITY_ENGAGEMENT,
        SKILL_DEVELOPMENT,
        PORTFOLIO_OPTIMIZATION,
        SPECIAL_RECOGNITION
    }
    
    public enum RarityLevel {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }
}

package com.yourcompany.portfoliogenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "activity_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String action;
    private String description;
    private String ipAddress;
    private String userAgent;
    private String sessionId;
    private ActivityType type;
    private String entityType;
    private String entityId;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
    
    public enum ActivityType {
        LOGIN,
        LOGOUT,
        REGISTRATION,
        PROFILE_UPDATE,
        TEMPLATE_SELECTION,
        PORTFOLIO_CREATION,
        PORTFOLIO_UPDATE,
        PORTFOLIO_DEPLOYMENT,
        PORTFOLIO_DELETE,
        RESUME_GENERATION,
        BADGE_EARNED,
        GITHUB_SYNC,
        LINKEDIN_SYNC,
        ADMIN_ACTION,
        SYSTEM_ACTION,
        ERROR
    }
}

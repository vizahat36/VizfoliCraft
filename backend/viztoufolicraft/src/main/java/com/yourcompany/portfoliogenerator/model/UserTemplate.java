package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "user_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTemplate {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private PortfolioTemplate template;
    
    private String customizedHtml;
    
    private String customizedCss;
    
    private String customizedJs;
    
    private String userData; // JSON data filled by user
    
    private boolean deployed = false;
    
    private String deploymentUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Lifecycle methods for MongoDB
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

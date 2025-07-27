package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "resume_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeTemplate {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private String description;
    
    private TemplateType templateType;
    
    private String htmlTemplate;
    
    private String cssStyles;
    
    private Boolean isPremium = false;
    
    private Boolean isActive = true;
    
    private String previewImageUrl;
    
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
    
    public enum TemplateType {
        CLASSIC,
        MODERN,
        CREATIVE,
        MINIMALIST,
        EXECUTIVE,
        TECHNICAL,
        ACADEMIC
    }
}

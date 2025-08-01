package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "portfolio_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioTemplate {
    
    @Id
    private String id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotBlank
    private String templateType; // e.g., "developer", "designer", "business", etc.
    
    private String htmlContent;
    
    private String cssContent;
    
    private String jsContent;
    
    private String jsonConfig; // Configuration for dynamic content
    
    private String previewImageUrl;
    
    private boolean active = true;
    
    private boolean featured = false;
    
    @DBRef
    private User createdBy;
    
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

package com.yourcompany.portfoliogenerator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotBlank
    @Column(name = "template_type", nullable = false)
    private String templateType; // e.g., "developer", "designer", "business", etc.
    
    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;
    
    @Column(name = "css_content", columnDefinition = "TEXT")
    private String cssContent;
    
    @Column(name = "js_content", columnDefinition = "TEXT")
    private String jsContent;
    
    @Column(name = "json_config", columnDefinition = "TEXT")
    private String jsonConfig; // Configuration for dynamic content
    
    @Column(name = "preview_image_url")
    private String previewImageUrl;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private boolean featured = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

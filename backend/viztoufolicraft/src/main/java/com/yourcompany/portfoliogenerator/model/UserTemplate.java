package com.yourcompany.portfoliogenerator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private PortfolioTemplate template;
    
    @Column(name = "customized_html", columnDefinition = "TEXT")
    private String customizedHtml;
    
    @Column(name = "customized_css", columnDefinition = "TEXT")
    private String customizedCss;
    
    @Column(name = "customized_js", columnDefinition = "TEXT")
    private String customizedJs;
    
    @Column(name = "user_data", columnDefinition = "TEXT")
    private String userData; // JSON data filled by user
    
    @Column(name = "is_deployed", nullable = false)
    private boolean deployed = false;
    
    @Column(name = "deployment_url")
    private String deploymentUrl;
    
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

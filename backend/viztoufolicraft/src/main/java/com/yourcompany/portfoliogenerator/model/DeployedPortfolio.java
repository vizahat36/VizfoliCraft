package com.yourcompany.portfoliogenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "deployed_portfolios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployedPortfolio {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private PortfolioTemplate template;
    
    private String deploymentId;
    private String publicUrl;
    private String subdomain;
    private String customDomain;
    private String title;
    private String description;
    private DeploymentStatus status;
    private DeploymentPlatform platform;
    
    // SEO Configuration
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    
    // Analytics
    private Long viewCount;
    private LocalDateTime lastViewed;
    private String analyticsId;
    
    // Configuration
    private String customCSS;
    private String customJS;
    private boolean isActive;
    private boolean isPublic;
    private boolean passwordProtected;
    private String password;
    
    // SSL and Performance
    private boolean sslEnabled;
    private String sslCertificate;
    private boolean cacheEnabled;
    private Integer cacheTTL;
    
    // Build Information
    private String buildVersion;
    private String buildLog;
    private LocalDateTime lastBuildTime;
    private LocalDateTime deployedAt;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
    
    public enum DeploymentStatus {
        PENDING,
        BUILDING,
        DEPLOYING,
        DEPLOYED,
        FAILED,
        UPDATING,
        DISABLED
    }
    
    public enum DeploymentPlatform {
        NETLIFY,
        VERCEL,
        AWS_S3,
        GITHUB_PAGES,
        INTERNAL_CDN,
        CUSTOM
    }
}

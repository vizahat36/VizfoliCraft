package com.yourcompany.portfoliogenerator.publicsite;

import lombok.Data;

/**
 * Request DTO for publishing portfolio
 */
@Data
public class PublishPortfolioRequest {
    
    private Boolean isPublic = true;
    private String customCSS;
    private String customJS;
}

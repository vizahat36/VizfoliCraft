package com.yourcompany.portfoliogenerator.publicsite;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for creating portfolio from GitHub/LinkedIn URL
 */
@Data
public class CreatePortfolioRequest {
    
    @NotBlank(message = "Profile URL is required")
    private String profileUrl;
    
    @NotBlank(message = "Platform is required (GITHUB or LINKEDIN)")
    private String platform;
}

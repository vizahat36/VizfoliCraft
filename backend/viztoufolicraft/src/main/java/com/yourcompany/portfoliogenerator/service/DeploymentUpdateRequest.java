package com.yourcompany.portfoliogenerator.service;

import lombok.Data;

@Data
public class DeploymentUpdateRequest {
    private String title;
    private String description;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String customCSS;
    private String customJS;
    private Boolean isPublic;
    private Boolean passwordProtected;
    private String password;
}

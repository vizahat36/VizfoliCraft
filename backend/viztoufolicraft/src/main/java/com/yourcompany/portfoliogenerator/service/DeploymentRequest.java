package com.yourcompany.portfoliogenerator.service;

import lombok.Data;

@Data
public class DeploymentRequest {
    private String subdomain;
    private String customDomain;
    private String title;
    private String description;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String customCSS;
    private String customJS;
    private boolean isPublic = true;
    private boolean passwordProtected = false;
    private String password;
    private boolean allowMultiple = false;
    private String platform = "INTERNAL_CDN";
}

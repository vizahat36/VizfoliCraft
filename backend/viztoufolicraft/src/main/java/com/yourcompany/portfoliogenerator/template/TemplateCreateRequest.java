package com.yourcompany.portfoliogenerator.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TemplateCreateRequest {
    
    @NotBlank(message = "Template name is required")
    @Size(max = 100, message = "Template name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Template type is required")
    private String templateType;
    
    private String htmlContent;
    
    private String cssContent;
    
    private String jsContent;
    
    private String jsonConfig;
    
    private String previewImageUrl;
    
    private boolean featured = false;
}

package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TemplateResponse {
    private Long id;
    private String name;
    private String description;
    private String templateType;
    private String htmlContent;
    private String cssContent;
    private String jsContent;
    private String jsonConfig;
    private String previewImageUrl;
    private boolean active;
    private boolean featured;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TemplateResponse fromTemplate(PortfolioTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateType(template.getTemplateType())
                .htmlContent(template.getHtmlContent())
                .cssContent(template.getCssContent())
                .jsContent(template.getJsContent())
                .jsonConfig(template.getJsonConfig())
                .previewImageUrl(template.getPreviewImageUrl())
                .active(template.isActive())
                .featured(template.isFeatured())
                .createdByUsername(template.getCreatedBy() != null ? template.getCreatedBy().getUsername() : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
    
    public static TemplateResponse fromTemplatePreview(PortfolioTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateType(template.getTemplateType())
                .previewImageUrl(template.getPreviewImageUrl())
                .featured(template.isFeatured())
                .createdByUsername(template.getCreatedBy() != null ? template.getCreatedBy().getUsername() : null)
                .createdAt(template.getCreatedAt())
                .build();
    }
}

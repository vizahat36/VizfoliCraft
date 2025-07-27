package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.UserTemplate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserTemplateResponse {
    private Long id;
    private Long templateId;
    private String templateName;
    private String templateType;
    private String customizedHtml;
    private String customizedCss;
    private String customizedJs;
    private String userData;
    private boolean deployed;
    private String deploymentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserTemplateResponse fromUserTemplate(UserTemplate userTemplate) {
        return UserTemplateResponse.builder()
                .id(userTemplate.getId())
                .templateId(userTemplate.getTemplate().getId())
                .templateName(userTemplate.getTemplate().getName())
                .templateType(userTemplate.getTemplate().getTemplateType())
                .customizedHtml(userTemplate.getCustomizedHtml())
                .customizedCss(userTemplate.getCustomizedCss())
                .customizedJs(userTemplate.getCustomizedJs())
                .userData(userTemplate.getUserData())
                .deployed(userTemplate.isDeployed())
                .deploymentUrl(userTemplate.getDeploymentUrl())
                .createdAt(userTemplate.getCreatedAt())
                .updatedAt(userTemplate.getUpdatedAt())
                .build();
    }
}

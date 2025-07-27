package com.yourcompany.portfoliogenerator.template;

import lombok.Data;

@Data
public class UserTemplateRequest {
    private Long templateId;
    private String customizedHtml;
    private String customizedCss;
    private String customizedJs;
    private String userData;
}

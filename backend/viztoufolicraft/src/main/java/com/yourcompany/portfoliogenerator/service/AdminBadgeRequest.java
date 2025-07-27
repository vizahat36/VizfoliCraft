package com.yourcompany.portfoliogenerator.service;

import lombok.Data;

@Data
public class AdminBadgeRequest {
    private String name;
    private String description;
    private String iconUrl;
    private String category;
    private Integer pointsRequired;
    private boolean isActive = true;
}

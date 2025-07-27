package com.yourcompany.portfoliogenerator.service;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AdminDashboardStats {
    private long totalUsers;
    private long activeUsers;
    private long totalDeployments;
    private long totalTemplates;
    private long totalBadges;
    private long recentUsers;
    private long recentDeployments;
    private long recentActivities;
    private Map<String, Long> topActivityTypes;
    private Map<String, Long> platformStats;
    private LocalDateTime lastUpdated;
}

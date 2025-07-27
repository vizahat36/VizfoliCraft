package com.yourcompany.portfoliogenerator.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsAggregated {
    private String userId;
    private String username;
    private String email;
    private Integer totalPoints;
    private Integer currentLevel;
    private Integer portfoliosCreated;
    private Integer resumesGenerated;
    private Integer profileCompletionPercentage;
    private Long badgeCount;
}

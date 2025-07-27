package com.yourcompany.portfoliogenerator.service;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PortfolioData {
    private PersonalInfo personalInfo;
    private List<Project> projects;
    private List<String> skills;
    private List<WorkExperience> experience;
    private List<Education> education;
    private SocialLinks socialLinks;
    private Stats stats;
    private LocalDateTime lastUpdated;
    
    @Data
    @Builder
    public static class PersonalInfo {
        private String name;
        private String title;
        private String bio;
        private String location;
        private String email;
        private String phone;
        private String profileImage;
        private String resume;
        private boolean availableForHire;
    }
    
    @Data
    @Builder
    public static class Project {
        private String name;
        private String description;
        private String url;
        private String githubUrl;
        private List<String> technologies;
        private Integer stars;
        private Integer forks;
        private LocalDateTime lastUpdated;
        private String status; // "active", "archived", "in-progress"
    }
    
    @Data
    @Builder
    public static class WorkExperience {
        private String title;
        private String company;
        private String description;
        private String startDate;
        private String endDate;
        private boolean current;
        private String location;
    }
    
    @Data
    @Builder
    public static class Education {
        private String degree;
        private String institution;
        private String field;
        private String startDate;
        private String endDate;
        private String description;
    }
    
    @Data
    @Builder
    public static class SocialLinks {
        private String linkedin;
        private String github;
        private String twitter;
        private String website;
    }
    
    @Data
    @Builder
    public static class Stats {
        private Integer totalProjects;
        private Integer totalStars;
        private Integer totalForks;
        private Integer totalRepositories;
        private Integer githubFollowers;
        private List<String> topLanguages;
        private Integer yearsOfExperience;
    }
}

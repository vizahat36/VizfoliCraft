package com.yourcompany.portfoliogenerator.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GitHubProfile {
    private String login;
    private String name;
    private String email;
    private String bio;
    private String blog;
    private String location;
    private String avatarUrl;
    private String htmlUrl;
    private Integer publicRepos;
    private Integer publicGists;
    private Integer followers;
    private Integer following;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Repository> repositories;
    private List<String> languages;
    
    @Data
    public static class Repository {
        private String name;
        private String fullName;
        private String description;
        private String htmlUrl;
        private String language;
        private Integer stargazersCount;
        private Integer forksCount;
        private boolean fork;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime pushedAt;
        private List<String> topics;
    }
}

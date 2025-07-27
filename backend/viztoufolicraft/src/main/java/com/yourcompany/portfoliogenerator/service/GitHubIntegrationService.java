package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubIntegrationService {
    
    private final WebClient.Builder webClientBuilder;
    private final UserProfileService userProfileService;
    
    @Value("${github.api.base-url:https://api.github.com}")
    private String githubApiBaseUrl;
    
    public Mono<GitHubProfile> fetchGitHubProfile(String username) {
        WebClient webClient = webClientBuilder
                .baseUrl(githubApiBaseUrl)
                .build();
        
        return webClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(userMap -> {
                    GitHubProfile profile = mapToGitHubProfile(userMap);
                    return fetchUserRepositories(username)
                            .map(repos -> {
                                profile.setRepositories(repos);
                                return profile;
                            });
                })
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> log.error("Error fetching GitHub profile for user {}: {}", username, error.getMessage()));
    }
    
    public Mono<List<GitHubProfile.Repository>> fetchUserRepositories(String username) {
        WebClient webClient = webClientBuilder
                .baseUrl(githubApiBaseUrl)
                .build();
        
        return webClient.get()
                .uri("/users/{username}/repos?sort=updated&per_page=50", username)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(this::mapToRepository)
                .collectList()
                .timeout(Duration.ofSeconds(30));
    }
    
    public Mono<UserProfileResponse> syncGitHubData(User user, String username) {
        return fetchGitHubProfile(username)
                .map(githubProfile -> {
                    UserProfileRequest profileUpdate = createProfileUpdateFromGitHub(githubProfile);
                    UserProfileResponse updatedProfile = userProfileService.createOrUpdateProfile(user, profileUpdate);
                    userProfileService.updateSyncStatus(user, "github", true);
                    return updatedProfile;
                })
                .doOnSuccess(profile -> log.info("Successfully synced GitHub data for user {}", user.getUsername()))
                .doOnError(error -> {
                    log.error("Failed to sync GitHub data for user {}: {}", user.getUsername(), error.getMessage());
                    userProfileService.updateSyncStatus(user, "github", false);
                });
    }
    
    private GitHubProfile mapToGitHubProfile(Map<String, Object> userMap) {
        GitHubProfile profile = new GitHubProfile();
        profile.setLogin((String) userMap.get("login"));
        profile.setName((String) userMap.get("name"));
        profile.setEmail((String) userMap.get("email"));
        profile.setBio((String) userMap.get("bio"));
        profile.setBlog((String) userMap.get("blog"));
        profile.setLocation((String) userMap.get("location"));
        profile.setAvatarUrl((String) userMap.get("avatar_url"));
        profile.setHtmlUrl((String) userMap.get("html_url"));
        profile.setPublicRepos((Integer) userMap.get("public_repos"));
        profile.setPublicGists((Integer) userMap.get("public_gists"));
        profile.setFollowers((Integer) userMap.get("followers"));
        profile.setFollowing((Integer) userMap.get("following"));
        return profile;
    }
    
    private GitHubProfile.Repository mapToRepository(Map<String, Object> repoMap) {
        GitHubProfile.Repository repo = new GitHubProfile.Repository();
        repo.setName((String) repoMap.get("name"));
        repo.setFullName((String) repoMap.get("full_name"));
        repo.setDescription((String) repoMap.get("description"));
        repo.setHtmlUrl((String) repoMap.get("html_url"));
        repo.setLanguage((String) repoMap.get("language"));
        repo.setStargazersCount((Integer) repoMap.get("stargazers_count"));
        repo.setForksCount((Integer) repoMap.get("forks_count"));
        repo.setFork((Boolean) repoMap.get("fork"));
        
        @SuppressWarnings("unchecked")
        List<String> topics = (List<String>) repoMap.get("topics");
        repo.setTopics(topics);
        
        return repo;
    }
    
    private UserProfileRequest createProfileUpdateFromGitHub(GitHubProfile githubProfile) {
        UserProfileRequest request = new UserProfileRequest();
        
        if (githubProfile.getName() != null) {
            request.setDisplayName(githubProfile.getName());
        }
        
        if (githubProfile.getBio() != null) {
            request.setBio(githubProfile.getBio());
        }
        
        if (githubProfile.getLocation() != null) {
            request.setLocation(githubProfile.getLocation());
        }
        
        if (githubProfile.getBlog() != null && !githubProfile.getBlog().isEmpty()) {
            request.setWebsite(githubProfile.getBlog());
        }
        
        if (githubProfile.getAvatarUrl() != null) {
            request.setProfileImageUrl(githubProfile.getAvatarUrl());
        }
        
        if (githubProfile.getHtmlUrl() != null) {
            request.setGithubUrl(githubProfile.getHtmlUrl());
        }
        
        // Extract skills from popular repository languages
        if (githubProfile.getRepositories() != null) {
            List<String> languages = githubProfile.getRepositories().stream()
                    .map(GitHubProfile.Repository::getLanguage)
                    .filter(lang -> lang != null && !lang.isEmpty())
                    .distinct()
                    .toList();
            
            if (!languages.isEmpty()) {
                request.setSkills(String.join(", ", languages));
            }
        }
        
        return request;
    }
}

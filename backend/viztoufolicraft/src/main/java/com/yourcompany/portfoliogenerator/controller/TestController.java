package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.service.GitHubIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @Value("${GITHUB_TOKEN:}")
    private String githubToken;

    private final WebClient.Builder webClientBuilder;

    /**
     * Test endpoint to verify GitHub token is working
     */
    @GetMapping("/github-token")
    public ResponseEntity<Map<String, Object>> testGitHubToken() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if token is configured
            boolean tokenConfigured = githubToken != null && !githubToken.trim().isEmpty();
            response.put("tokenConfigured", tokenConfigured);
            
            if (tokenConfigured) {
                response.put("tokenLength", githubToken.length());
                response.put("tokenPrefix", githubToken.substring(0, Math.min(20, githubToken.length())) + "...");
                response.put("status", "GitHub token is configured");
            } else {
                response.put("status", "GitHub token is not configured");
                response.put("message", "Set GITHUB_TOKEN in your .env file");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error testing GitHub token", e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test GitHub API call with the configured token
     */
    @GetMapping("/github-api/{username}")
    public Mono<ResponseEntity<Map<String, Object>>> testGitHubApi(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        
        if (githubToken == null || githubToken.trim().isEmpty()) {
            response.put("error", "GitHub token not configured");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        WebClient webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "token " + githubToken)
                .defaultHeader("User-Agent", "VizfoliCraft-Portfolio-Generator")
                .build();

        return webClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .bodyToMono(Map.class)
                .map(userInfo -> {
                    response.put("success", true);
                    response.put("username", userInfo.get("login"));
                    response.put("name", userInfo.get("name"));
                    response.put("publicRepos", userInfo.get("public_repos"));
                    response.put("followers", userInfo.get("followers"));
                    response.put("following", userInfo.get("following"));
                    response.put("location", userInfo.get("location"));
                    response.put("bio", userInfo.get("bio"));
                    response.put("blog", userInfo.get("blog"));
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error calling GitHub API for user {}: {}", username, error.getMessage());
                    response.put("error", "GitHub API call failed: " + error.getMessage());
                    response.put("success", false);
                    return Mono.just(ResponseEntity.internalServerError().body(response));
                });
    }

    /**
     * Test GitHub repositories API
     */
    @GetMapping("/github-repos/{username}")
    public Mono<ResponseEntity<Map<String, Object>>> testGitHubRepos(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        
        if (githubToken == null || githubToken.trim().isEmpty()) {
            response.put("error", "GitHub token not configured");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        WebClient webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "token " + githubToken)
                .defaultHeader("User-Agent", "VizfoliCraft-Portfolio-Generator")
                .build();

        return webClient.get()
                .uri("/users/{username}/repos?sort=updated&per_page=10", username)
                .retrieve()
                .bodyToMono(Object[].class)
                .map(repos -> {
                    response.put("success", true);
                    response.put("repositoryCount", repos.length);
                    response.put("repositories", repos);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error fetching GitHub repos for user {}: {}", username, error.getMessage());
                    response.put("error", "GitHub repos API call failed: " + error.getMessage());
                    response.put("success", false);
                    return Mono.just(ResponseEntity.internalServerError().body(response));
                });
    }
}

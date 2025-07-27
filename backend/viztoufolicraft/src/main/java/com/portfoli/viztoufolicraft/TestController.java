package com.portfoli.viztoufolicraft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple test controller to verify GitHub token integration
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final WebClient webClient;
    
    @Value("${github.token}")
    private String githubToken;

    public TestController() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    /**
     * Test endpoint to verify GitHub token is loaded correctly
     */
    @GetMapping("/github-token")
    public ResponseEntity<Map<String, Object>> testGitHubToken() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if token is loaded
            if (githubToken != null && !githubToken.isEmpty() && !githubToken.startsWith("${")) {
                response.put("status", "SUCCESS");
                response.put("message", "GitHub token is loaded correctly");
                response.put("tokenLength", githubToken.length());
                response.put("tokenPrefix", githubToken.substring(0, Math.min(20, githubToken.length())) + "...");
            } else {
                response.put("status", "ERROR");
                response.put("message", "GitHub token is not loaded or empty");
                response.put("tokenValue", githubToken);
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error checking GitHub token: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test GitHub API call with the loaded token
     */
    @GetMapping("/github-api/{username}")
    public Mono<ResponseEntity<Map<String, Object>>> testGitHubApi(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if token is available
        if (githubToken == null || githubToken.isEmpty() || githubToken.startsWith("${")) {
            response.put("status", "ERROR");
            response.put("message", "GitHub token not configured");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        return webClient.get()
                .uri("/users/{username}", username)
                .header("Authorization", "token " + githubToken)
                .retrieve()
                .bodyToMono(Map.class)
                .map(apiResponse -> {
                    response.put("status", "SUCCESS");
                    response.put("message", "GitHub API call successful");
                    response.put("user", apiResponse);
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(error -> {
                    response.put("status", "ERROR");
                    response.put("message", "GitHub API call failed: " + error.getMessage());
                    return ResponseEntity.status(500).body(response);
                });
    }

    /**
     * Test GitHub repositories API
     */
    @GetMapping("/github-repos/{username}")
    public Mono<ResponseEntity<Map<String, Object>>> testGitHubRepos(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if token is available
        if (githubToken == null || githubToken.isEmpty() || githubToken.startsWith("${")) {
            response.put("status", "ERROR");
            response.put("message", "GitHub token not configured");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        return webClient.get()
                .uri("/users/{username}/repos?per_page=5", username)
                .header("Authorization", "token " + githubToken)
                .retrieve()
                .bodyToMono(Object[].class)
                .map(repos -> {
                    response.put("status", "SUCCESS");
                    response.put("message", "GitHub repos API call successful");
                    response.put("repoCount", repos.length);
                    response.put("repositories", repos);
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(error -> {
                    response.put("status", "ERROR");
                    response.put("message", "GitHub repos API call failed: " + error.getMessage());
                    return ResponseEntity.status(500).body(response);
                });
    }
}

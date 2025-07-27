package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserProfile;
import com.yourcompany.portfoliogenerator.service.PortfolioDataGenerationService;
import com.yourcompany.portfoliogenerator.service.UserProfileService;
import com.yourcompany.portfoliogenerator.service.GitHubIntegrationService;
import com.yourcompany.portfoliogenerator.service.LinkedInIntegrationService;
import com.yourcompany.portfoliogenerator.service.PortfolioData;
import com.yourcompany.portfoliogenerator.service.GitHubProfile;
import com.yourcompany.portfoliogenerator.service.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user-profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    private final PortfolioDataGenerationService portfolioDataGenerationService;
    private final GitHubIntegrationService gitHubIntegrationService;
    private final LinkedInIntegrationService linkedInIntegrationService;
    
    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfile profile = userProfileService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }
    
    @PostMapping
    public ResponseEntity<UserProfile> createOrUpdateUserProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserProfile profileRequest) {
        UserProfile profile = userProfileService.createOrUpdateUserProfile(user, profileRequest);
        log.info("Created/updated profile for user: {}", user.getUsername());
        return ResponseEntity.ok(profile);
    }
    
    @PatchMapping
    public ResponseEntity<UserProfile> updateUserProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> updates) {
        UserProfile profile = userProfileService.updateUserProfile(user, updates);
        log.info("Partially updated profile for user: {}", user.getUsername());
        return ResponseEntity.ok(profile);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfile(@AuthenticationPrincipal User user) {
        userProfileService.deleteUserProfile(user);
        log.info("Deleted profile for user: {}", user.getUsername());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/portfolio-data")
    public Mono<ResponseEntity<PortfolioData>> getPortfolioData(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "false") boolean syncGitHub,
            @RequestParam(defaultValue = "false") boolean syncLinkedIn) {
        
        if (syncGitHub || syncLinkedIn) {
            return portfolioDataGenerationService.generatePortfolioDataWithSync(user, syncGitHub, syncLinkedIn)
                    .map(ResponseEntity::ok)
                    .doOnNext(response -> log.info("Generated portfolio data with sync for user: {}", user.getUsername()))
                    .onErrorReturn(ResponseEntity.internalServerError().build());
        } else {
            return portfolioDataGenerationService.generatePortfolioData(user)
                    .map(ResponseEntity::ok)
                    .doOnNext(response -> log.info("Generated portfolio data for user: {}", user.getUsername()))
                    .onErrorReturn(ResponseEntity.internalServerError().build());
        }
    }
    
    @PostMapping("/sync/github")
    public Mono<ResponseEntity<GitHubProfile>> syncGitHub(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        String githubUrl = request.get("githubUrl");
        if (githubUrl == null || githubUrl.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        // Extract username from URL
        String username = extractGitHubUsername(githubUrl);
        if (username == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return gitHubIntegrationService.fetchGitHubProfile(username)
                .map(profile -> {
                    // Update user profile with GitHub URL if not already set
                    UserProfile userProfile = userProfileService.getUserProfile(user);
                    if (userProfile.getGithubUrl() == null || !userProfile.getGithubUrl().equals(githubUrl)) {
                        userProfile.setGithubUrl(githubUrl);
                        userProfile.setGithubSyncedAt(java.time.LocalDateTime.now());
                        userProfileService.updateUserProfile(user, userProfile);
                    }
                    
                    log.info("Synced GitHub data for user: {} with username: {}", user.getUsername(), username);
                    return ResponseEntity.ok(profile);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    @PostMapping("/sync/linkedin")
    public Mono<ResponseEntity<LinkedInProfile>> syncLinkedIn(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        String linkedinUrl = request.get("linkedinUrl");
        if (linkedinUrl == null || linkedinUrl.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return linkedInIntegrationService.fetchLinkedInProfile(linkedinUrl)
                .map(profile -> {
                    // Update user profile with LinkedIn URL if not already set
                    UserProfile userProfile = userProfileService.getUserProfile(user);
                    if (userProfile.getLinkedinUrl() == null || !userProfile.getLinkedinUrl().equals(linkedinUrl)) {
                        userProfile.setLinkedinUrl(linkedinUrl);
                        userProfile.setLinkedinSyncedAt(java.time.LocalDateTime.now());
                        userProfileService.updateUserProfile(user, userProfile);
                    }
                    
                    log.info("Synced LinkedIn data for user: {}", user.getUsername());
                    return ResponseEntity.ok(profile);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    @PostMapping("/sync/both")
    public Mono<ResponseEntity<Map<String, Object>>> syncBothSocialPlatforms(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        String githubUrl = request.get("githubUrl");
        String linkedinUrl = request.get("linkedinUrl");
        
        Mono<GitHubProfile> githubMono = Mono.empty();
        Mono<LinkedInProfile> linkedinMono = Mono.empty();
        
        if (githubUrl != null && !githubUrl.trim().isEmpty()) {
            String githubUsername = extractGitHubUsername(githubUrl);
            if (githubUsername != null) {
                githubMono = gitHubIntegrationService.fetchGitHubProfile(githubUsername)
                        .onErrorReturn(new GitHubProfile()); // Return empty profile on error
            }
        }
        
        if (linkedinUrl != null && !linkedinUrl.trim().isEmpty()) {
            linkedinMono = linkedInIntegrationService.fetchLinkedInProfile(linkedinUrl)
                    .onErrorReturn(new LinkedInProfile()); // Return empty profile on error
        }
        
        return Mono.zip(githubMono.defaultIfEmpty(new GitHubProfile()), 
                       linkedinMono.defaultIfEmpty(new LinkedInProfile()))
                .map(tuple -> {
                    GitHubProfile githubProfile = tuple.getT1();
                    LinkedInProfile linkedinProfile = tuple.getT2();
                    
                    // Update user profile with both URLs
                    UserProfile userProfile = userProfileService.getUserProfile(user);
                    boolean updated = false;
                    
                    if (githubUrl != null && !githubUrl.trim().isEmpty()) {
                        userProfile.setGithubUrl(githubUrl);
                        userProfile.setGithubSyncedAt(java.time.LocalDateTime.now());
                        updated = true;
                    }
                    
                    if (linkedinUrl != null && !linkedinUrl.trim().isEmpty()) {
                        userProfile.setLinkedinUrl(linkedinUrl);
                        userProfile.setLinkedinSyncedAt(java.time.LocalDateTime.now());
                        updated = true;
                    }
                    
                    if (updated) {
                        userProfileService.updateUserProfile(user, userProfile);
                    }
                    
                    Map<String, Object> result = Map.of(
                            "github", githubProfile,
                            "linkedin", linkedinProfile,
                            "syncSuccess", true
                    );
                    
                    log.info("Synced both social platforms for user: {}", user.getUsername());
                    return ResponseEntity.ok(result);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    @GetMapping("/sync-status")
    public ResponseEntity<Map<String, Object>> getSyncStatus(@AuthenticationPrincipal User user) {
        UserProfile profile = userProfileService.getUserProfile(user);
        
        Map<String, Object> status = Map.of(
                "githubSynced", profile.getGithubUrl() != null,
                "githubLastSync", profile.getGithubSyncedAt(),
                "linkedinSynced", profile.getLinkedinUrl() != null,
                "linkedinLastSync", profile.getLinkedinSyncedAt(),
                "githubUrl", profile.getGithubUrl() != null ? profile.getGithubUrl() : "",
                "linkedinUrl", profile.getLinkedinUrl() != null ? profile.getLinkedinUrl() : ""
        );
        
        return ResponseEntity.ok(status);
    }
    
    private String extractGitHubUsername(String githubUrl) {
        if (githubUrl == null || !githubUrl.contains("github.com/")) {
            return null;
        }
        
        try {
            String[] parts = githubUrl.split("github.com/");
            if (parts.length > 1) {
                String username = parts[1].split("/")[0].split("\\?")[0];
                return username.isEmpty() ? null : username;
            }
        } catch (Exception e) {
            log.error("Error extracting username from GitHub URL: {}", e.getMessage());
        }
        
        return null;
    }
}

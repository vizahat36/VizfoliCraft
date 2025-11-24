package com.yourcompany.portfoliogenerator.publicsite;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for simplified portfolio creation workflow
 * Supports the frontend flow: GitHub/LinkedIn URL → Import → Edit → Publish
 */
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class SimplePortfolioController {

    private final GitHubIntegrationService gitHubService;
    private final LinkedInIntegrationService linkedInService;
    private final UserProfileService userProfileService;

    /**
     * Create portfolio by importing from GitHub or LinkedIn URL
     * POST /api/portfolio/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("Creating portfolio for user {} from {} URL: {}", 
                user.getUsername(), request.getPlatform(), request.getProfileUrl());

            // Extract username from URL
            String username = extractUsernameFromUrl(request.getProfileUrl(), request.getPlatform());
            
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid profile URL. Could not extract username.")
                );
            }

            // Fetch profile data based on platform
            UserProfileResponse profileData;
            if ("GITHUB".equalsIgnoreCase(request.getPlatform())) {
                profileData = gitHubService.syncGitHubData(user, username).block();
            } else if ("LINKEDIN".equalsIgnoreCase(request.getPlatform())) {
                profileData = linkedInService.syncLinkedInDataFromUrl(user, request.getProfileUrl()).block();
            } else {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid platform. Use GITHUB or LINKEDIN.")
                );
            }

            if (profileData == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Failed to fetch profile data. Please check the URL and try again.")
                );
            }

            log.info("Successfully imported profile data for user {}", user.getUsername());
            return ResponseEntity.ok(profileData);

        } catch (Exception e) {
            log.error("Error creating portfolio for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Failed to import profile data: " + e.getMessage())
            );
        }
    }

    /**
     * Get current user's portfolio
     * GET /api/portfolio/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyPortfolio(@AuthenticationPrincipal User user) {
        try {
            UserProfileResponse profile = userProfileService.getUserProfileResponse(user);
            
            if (profile == null) {
                return ResponseEntity.notFound().build();
            }

            // Add publication status
            Map<String, Object> response = new HashMap<>();
            response.put("profile", profile);
            response.put("isPublished", false); // TODO: Get from deployment service
            response.put("publicUrl", "https://portfolicraft.me/u/" + user.getUsername());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching portfolio for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Failed to fetch portfolio: " + e.getMessage())
            );
        }
    }

    /**
     * Update user's portfolio
     * PUT /api/portfolio/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> updatePortfolio(
            @Valid @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal User user) {
        
        try {
            UserProfileResponse updatedProfile = userProfileService.createOrUpdateProfile(user, request);
            
            return ResponseEntity.ok(Map.of(
                "id", updatedProfile.getId(),
                "message", "Portfolio updated successfully",
                "profile", updatedProfile
            ));

        } catch (Exception e) {
            log.error("Error updating portfolio for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Failed to update portfolio: " + e.getMessage())
            );
        }
    }

    /**
     * Publish user's portfolio
     * POST /api/portfolio/publish
     */
    @PostMapping("/publish")
    public ResponseEntity<?> publishPortfolio(
            @Valid @RequestBody PublishPortfolioRequest request,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("Publishing portfolio for user {}", user.getUsername());

            // Get user's profile
            UserProfileResponse profile = userProfileService.getUserProfileResponse(user);
            
            if (profile == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Please create a portfolio first before publishing.")
                );
            }

            // For now, just mark as published and return success
            // In a full implementation, this would integrate with the deployment service
            String publicUrl = "https://portfolicraft.me/u/" + user.getUsername();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "publicUrl", publicUrl,
                "message", "Portfolio published successfully"
            ));

        } catch (Exception e) {
            log.error("Error publishing portfolio for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Failed to publish portfolio: " + e.getMessage())
            );
        }
    }

    /**
     * Extract username from GitHub or LinkedIn URL
     */
    private String extractUsernameFromUrl(String url, String platform) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Remove trailing slashes
        url = url.replaceAll("/+$", "");

        if ("GITHUB".equalsIgnoreCase(platform)) {
            // GitHub URL patterns:
            // https://github.com/username
            // http://github.com/username
            // github.com/username
            if (url.contains("github.com/")) {
                String[] parts = url.split("github\\.com/");
                if (parts.length > 1) {
                    String username = parts[1].split("/")[0];
                    return username;
                }
            }
        } else if ("LINKEDIN".equalsIgnoreCase(platform)) {
            // LinkedIn URL patterns:
            // https://linkedin.com/in/username
            // https://www.linkedin.com/in/username
            if (url.contains("linkedin.com/in/")) {
                String[] parts = url.split("linkedin\\.com/in/");
                if (parts.length > 1) {
                    String username = parts[1].split("/")[0];
                    return username;
                }
            }
        }

        return null;
    }
}

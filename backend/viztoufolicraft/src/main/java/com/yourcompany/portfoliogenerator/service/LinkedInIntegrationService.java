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
public class LinkedInIntegrationService {
    
    private final WebClient.Builder webClientBuilder;
    private final UserProfileService userProfileService;
    
    @Value("${linkedin.api.base-url:https://api.linkedin.com}")
    private String linkedinApiBaseUrl;
    
    /**
     * Note: This is a simplified version. In production, you would need:
     * 1. LinkedIn OAuth 2.0 authentication
     * 2. User consent for data access
     * 3. Access tokens with proper scopes
     * 4. Rate limiting and error handling
     */
    public Mono<LinkedInProfile> fetchLinkedInProfile(String accessToken) {
        WebClient webClient = webClientBuilder
                .baseUrl(linkedinApiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
        
        return webClient.get()
                .uri("/v2/people/~")
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToLinkedInProfile)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> log.error("Error fetching LinkedIn profile: {}", error.getMessage()));
    }
    
    public Mono<UserProfileResponse> syncLinkedInData(User user, String accessToken) {
        return fetchLinkedInProfile(accessToken)
                .map(linkedinProfile -> {
                    UserProfileRequest profileUpdate = createProfileUpdateFromLinkedIn(linkedinProfile);
                    UserProfileResponse updatedProfile = userProfileService.createOrUpdateProfile(user, profileUpdate);
                    userProfileService.updateSyncStatus(user, "linkedin", true);
                    return updatedProfile;
                })
                .doOnSuccess(profile -> log.info("Successfully synced LinkedIn data for user {}", user.getUsername()))
                .doOnError(error -> {
                    log.error("Failed to sync LinkedIn data for user {}: {}", user.getUsername(), error.getMessage());
                    userProfileService.updateSyncStatus(user, "linkedin", false);
                });
    }
    
    /**
     * For demonstration purposes - parses LinkedIn profile URL to extract public data
     * In production, this should be replaced with proper OAuth flow
     */
    public Mono<UserProfileResponse> syncLinkedInDataFromUrl(User user, String linkedinUrl) {
        // Extract username from LinkedIn URL
        String username = extractUsernameFromLinkedInUrl(linkedinUrl);
        
        if (username == null) {
            return Mono.error(new IllegalArgumentException("Invalid LinkedIn URL"));
        }
        
        // For now, just update the LinkedIn URL in the profile
        UserProfileRequest profileUpdate = new UserProfileRequest();
        profileUpdate.setLinkedinUrl(linkedinUrl);
        
        UserProfileResponse updatedProfile = userProfileService.createOrUpdateProfile(user, profileUpdate);
        userProfileService.updateSyncStatus(user, "linkedin", true);
        
        return Mono.just(updatedProfile);
    }
    
    private LinkedInProfile mapToLinkedInProfile(Map<String, Object> profileMap) {
        LinkedInProfile profile = new LinkedInProfile();
        
        // Map basic profile information
        if (profileMap.containsKey("localizedFirstName")) {
            profile.setFirstName((String) profileMap.get("localizedFirstName"));
        }
        
        if (profileMap.containsKey("localizedLastName")) {
            profile.setLastName((String) profileMap.get("localizedLastName"));
        }
        
        if (profileMap.containsKey("headline")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> headline = (Map<String, Object>) profileMap.get("headline");
            if (headline.containsKey("localized")) {
                @SuppressWarnings("unchecked")
                Map<String, String> localized = (Map<String, String>) headline.get("localized");
                profile.setHeadline(localized.values().iterator().next());
            }
        }
        
        return profile;
    }
    
    private UserProfileRequest createProfileUpdateFromLinkedIn(LinkedInProfile linkedinProfile) {
        UserProfileRequest request = new UserProfileRequest();
        
        if (linkedinProfile.getFirstName() != null && linkedinProfile.getLastName() != null) {
            request.setDisplayName(linkedinProfile.getFirstName() + " " + linkedinProfile.getLastName());
        }
        
        if (linkedinProfile.getHeadline() != null) {
            request.setProfession(linkedinProfile.getHeadline());
        }
        
        if (linkedinProfile.getSummary() != null) {
            request.setBio(linkedinProfile.getSummary());
        }
        
        if (linkedinProfile.getLocation() != null) {
            request.setLocation(linkedinProfile.getLocation().getName());
        }
        
        if (linkedinProfile.getSkills() != null && !linkedinProfile.getSkills().isEmpty()) {
            request.setSkills(String.join(", ", linkedinProfile.getSkills()));
        }
        
        // Convert positions to experience
        if (linkedinProfile.getPositions() != null && !linkedinProfile.getPositions().isEmpty()) {
            StringBuilder experience = new StringBuilder();
            for (LinkedInProfile.Position position : linkedinProfile.getPositions()) {
                experience.append(position.getTitle()).append(" at ").append(position.getCompanyName());
                if (position.getStartDate() != null) {
                    experience.append(" (").append(position.getStartDate());
                    if (position.getEndDate() != null) {
                        experience.append(" - ").append(position.getEndDate());
                    } else if (position.isCurrent()) {
                        experience.append(" - Present");
                    }
                    experience.append(")");
                }
                if (position.getDescription() != null) {
                    experience.append(": ").append(position.getDescription());
                }
                experience.append("\n\n");
            }
            request.setExperience(experience.toString().trim());
        }
        
        // Convert education
        if (linkedinProfile.getEducations() != null && !linkedinProfile.getEducations().isEmpty()) {
            StringBuilder education = new StringBuilder();
            for (LinkedInProfile.Education edu : linkedinProfile.getEducations()) {
                education.append(edu.getDegreeName());
                if (edu.getFieldOfStudy() != null) {
                    education.append(" in ").append(edu.getFieldOfStudy());
                }
                education.append(" from ").append(edu.getSchoolName());
                if (edu.getStartDate() != null && edu.getEndDate() != null) {
                    education.append(" (").append(edu.getStartDate()).append(" - ").append(edu.getEndDate()).append(")");
                }
                education.append("\n\n");
            }
            request.setEducation(education.toString().trim());
        }
        
        return request;
    }
    
    private String extractUsernameFromLinkedInUrl(String url) {
        if (url == null || !url.contains("linkedin.com/in/")) {
            return null;
        }
        
        try {
            String[] parts = url.split("linkedin.com/in/");
            if (parts.length > 1) {
                String username = parts[1].split("/")[0].split("\\?")[0];
                return username.isEmpty() ? null : username;
            }
        } catch (Exception e) {
            log.error("Error extracting username from LinkedIn URL: {}", e.getMessage());
        }
        
        return null;
    }
}

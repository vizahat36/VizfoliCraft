package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String displayName;
    private String profession;
    private String location;
    private String bio;
    private String profileImageUrl;
    private String resumeUrl;
    private String phoneNumber;
    private String website;
    private String linkedinUrl;
    private String githubUrl;
    private String twitterUrl;
    private String skills;
    private String experience;
    private String education;
    private String certifications;
    private Integer yearsOfExperience;
    private Boolean availableForHire;
    private Boolean linkedinSynced;
    private Boolean githubSynced;
    private LocalDateTime lastLinkedinSync;
    private LocalDateTime lastGithubSync;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserProfileResponse fromUserProfile(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .displayName(profile.getDisplayName())
                .profession(profile.getProfession())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .resumeUrl(profile.getResumeUrl())
                .phoneNumber(profile.getPhoneNumber())
                .website(profile.getWebsite())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .twitterUrl(profile.getTwitterUrl())
                .skills(profile.getSkills())
                .experience(profile.getExperience())
                .education(profile.getEducation())
                .certifications(profile.getCertifications())
                .yearsOfExperience(profile.getYearsOfExperience())
                .availableForHire(profile.getAvailableForHire())
                .linkedinSynced(profile.getLinkedinSynced())
                .githubSynced(profile.getGithubSynced())
                .lastLinkedinSync(profile.getLastLinkedinSync())
                .lastGithubSync(profile.getLastGithubSync())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

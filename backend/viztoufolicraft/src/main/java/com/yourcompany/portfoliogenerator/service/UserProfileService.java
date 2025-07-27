package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserProfile;
import com.yourcompany.portfoliogenerator.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    public UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElse(null);
    }
    
    public UserProfileResponse getUserProfileResponse(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);
        return profile != null ? UserProfileResponse.fromUserProfile(profile) : null;
    }
    
    public UserProfile createOrUpdateUserProfile(User user, UserProfile profileRequest) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            return updateUserProfile(user, profileRequest);
        } else {
            profileRequest.setUser(user);
            profileRequest.setCreatedAt(LocalDateTime.now());
            profileRequest.setUpdatedAt(LocalDateTime.now());
            UserProfile saved = userProfileRepository.save(profileRequest);
            log.info("Created new profile for user: {}", user.getEmail());
            return saved;
        }
    }
    
    public UserProfileResponse createOrUpdateProfile(User user, UserProfileRequest request) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);
        
        UserProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            updateProfileFromRequest(profile, request);
        } else {
            profile = createProfileFromRequest(user, request);
        }
        
        UserProfile saved = userProfileRepository.save(profile);
        log.info("Created/updated profile for user: {}", user.getEmail());
        return UserProfileResponse.fromUserProfile(saved);
    }
    
    public UserProfile updateUserProfile(User user, UserProfile updates) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            UserProfile profile = existingProfile.get();
            updateProfileFields(profile, updates);
            profile.setUpdatedAt(LocalDateTime.now());
            UserProfile saved = userProfileRepository.save(profile);
            log.info("Updated profile for user: {}", user.getEmail());
            return saved;
        } else {
            throw new RuntimeException("Profile not found for user: " + user.getEmail());
        }
    }
    
    public UserProfileResponse updateProfile(User user, UserProfileRequest request) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            UserProfile profile = existingProfile.get();
            updateProfileFromRequest(profile, request);
            UserProfile saved = userProfileRepository.save(profile);
            log.info("Updated profile for user: {}", user.getEmail());
            return UserProfileResponse.fromUserProfile(saved);
        } else {
            throw new RuntimeException("Profile not found for user: " + user.getEmail());
        }
    }
    
    public void deleteUserProfile(User user) {
        Optional<UserProfile> profile = userProfileRepository.findByUser(user);
        if (profile.isPresent()) {
            userProfileRepository.delete(profile.get());
            log.info("Deleted profile for user: {}", user.getEmail());
        }
    }
    
    public boolean deleteProfile(User user) {
        Optional<UserProfile> profile = userProfileRepository.findByUser(user);
        if (profile.isPresent()) {
            userProfileRepository.delete(profile.get());
            log.info("Deleted profile for user: {}", user.getEmail());
            return true;
        }
        return false;
    }
    
    public UserProfileResponse updateSyncStatus(User user, String platform, boolean synced) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            UserProfile profile = existingProfile.get();
            LocalDateTime now = LocalDateTime.now();
            
            switch (platform.toLowerCase()) {
                case "linkedin":
                    profile.setLinkedinSynced(synced);
                    if (synced) {
                        profile.setLastLinkedinSync(now);
                        profile.setLinkedinSyncedAt(now);
                    }
                    break;
                case "github":
                    profile.setGithubSynced(synced);
                    if (synced) {
                        profile.setLastGithubSync(now);
                        profile.setGithubSyncedAt(now);
                    }
                    break;
                default:
                    log.warn("Unknown platform for sync status update: {}", platform);
                    break;
            }
            
            profile.setUpdatedAt(now);
            UserProfile saved = userProfileRepository.save(profile);
            log.info("Updated {} sync status to {} for user: {}", platform, synced, user.getEmail());
            return UserProfileResponse.fromUserProfile(saved);
        } else {
            throw new RuntimeException("Profile not found for user: " + user.getEmail());
        }
    }
    
    private void updateProfileFromRequest(UserProfile profile, UserProfileRequest request) {
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getProfession() != null) profile.setProfession(request.getProfession());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getProfileImageUrl() != null) profile.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getResumeUrl() != null) profile.setResumeUrl(request.getResumeUrl());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getTwitterUrl() != null) profile.setTwitterUrl(request.getTwitterUrl());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getExperience() != null) profile.setExperience(request.getExperience());
        if (request.getEducation() != null) profile.setEducation(request.getEducation());
        if (request.getCertifications() != null) profile.setCertifications(request.getCertifications());
        if (request.getYearsOfExperience() != null) profile.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getAvailableForHire() != null) profile.setAvailableForHire(request.getAvailableForHire());
        
        profile.setUpdatedAt(LocalDateTime.now());
    }
    
    private UserProfile createProfileFromRequest(User user, UserProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        updateProfileFromRequest(profile, request);
        profile.setCreatedAt(LocalDateTime.now());
        return profile;
    }
    
    private void updateProfileFields(UserProfile target, UserProfile source) {
        if (source.getDisplayName() != null) target.setDisplayName(source.getDisplayName());
        if (source.getProfession() != null) target.setProfession(source.getProfession());
        if (source.getLocation() != null) target.setLocation(source.getLocation());
        if (source.getBio() != null) target.setBio(source.getBio());
        if (source.getProfileImageUrl() != null) target.setProfileImageUrl(source.getProfileImageUrl());
        if (source.getResumeUrl() != null) target.setResumeUrl(source.getResumeUrl());
        if (source.getPhoneNumber() != null) target.setPhoneNumber(source.getPhoneNumber());
        if (source.getWebsite() != null) target.setWebsite(source.getWebsite());
        if (source.getLinkedinUrl() != null) target.setLinkedinUrl(source.getLinkedinUrl());
        if (source.getGithubUrl() != null) target.setGithubUrl(source.getGithubUrl());
        if (source.getTwitterUrl() != null) target.setTwitterUrl(source.getTwitterUrl());
        if (source.getSkills() != null) target.setSkills(source.getSkills());
        if (source.getExperience() != null) target.setExperience(source.getExperience());
        if (source.getEducation() != null) target.setEducation(source.getEducation());
        if (source.getCertifications() != null) target.setCertifications(source.getCertifications());
        if (source.getYearsOfExperience() != null) target.setYearsOfExperience(source.getYearsOfExperience());
        if (source.getAvailableForHire() != null) target.setAvailableForHire(source.getAvailableForHire());
        if (source.getLinkedinSynced() != null) target.setLinkedinSynced(source.getLinkedinSynced());
        if (source.getGithubSynced() != null) target.setGithubSynced(source.getGithubSynced());
        if (source.getLastLinkedinSync() != null) target.setLastLinkedinSync(source.getLastLinkedinSync());
        if (source.getLastGithubSync() != null) target.setLastGithubSync(source.getLastGithubSync());
        if (source.getLinkedinSyncedAt() != null) target.setLinkedinSyncedAt(source.getLinkedinSyncedAt());
        if (source.getGithubSyncedAt() != null) target.setGithubSyncedAt(source.getGithubSyncedAt());
    }
}

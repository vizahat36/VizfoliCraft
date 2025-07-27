package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @Size(max = 100)
    private String displayName;
    
    @Size(max = 100)
    private String profession;
    
    @Size(max = 200)
    private String location;
    
    private String bio;
    
    @Size(max = 200)
    private String profileImageUrl;
    
    @Size(max = 200)
    private String resumeUrl;
    
    @Size(max = 100)
    private String phoneNumber;
    
    @Size(max = 200)
    private String website;
    
    @Size(max = 200)
    private String linkedinUrl;
    
    @Size(max = 200)
    private String githubUrl;
    
    @Size(max = 200)
    private String twitterUrl;
    
    private String skills;
    
    private String experience;
    
    private String education;
    
    private String certifications;
    
    private Integer yearsOfExperience;
    
    private Boolean availableForHire = false;
    
    private Boolean linkedinSynced = false;
    
    private Boolean githubSynced = false;
    
    private LocalDateTime lastLinkedinSync;
    
    private LocalDateTime lastGithubSync;
    
    private LocalDateTime linkedinSyncedAt;
    
    private LocalDateTime githubSyncedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Lifecycle methods for MongoDB
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

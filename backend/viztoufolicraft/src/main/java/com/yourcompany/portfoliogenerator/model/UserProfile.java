package com.yourcompany.portfoliogenerator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Size(max = 100)
    private String displayName;
    
    @Size(max = 100)
    private String profession;
    
    @Size(max = 200)
    private String location;
    
    @Column(columnDefinition = "TEXT")
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
    
    @Column(columnDefinition = "TEXT")
    private String skills;
    
    @Column(columnDefinition = "TEXT")
    private String experience;
    
    @Column(columnDefinition = "TEXT")
    private String education;
    
    @Column(columnDefinition = "TEXT")
    private String certifications;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "is_available_for_hire")
    private Boolean availableForHire = false;
    
    @Column(name = "linkedin_synced")
    private Boolean linkedinSynced = false;
    
    @Column(name = "github_synced")
    private Boolean githubSynced = false;
    
    @Column(name = "last_linkedin_sync")
    private LocalDateTime lastLinkedinSync;
    
    @Column(name = "last_github_sync")
    private LocalDateTime lastGithubSync;
    
    @Column(name = "linkedin_synced_at")
    private LocalDateTime linkedinSyncedAt;
    
    @Column(name = "github_synced_at")
    private LocalDateTime githubSyncedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

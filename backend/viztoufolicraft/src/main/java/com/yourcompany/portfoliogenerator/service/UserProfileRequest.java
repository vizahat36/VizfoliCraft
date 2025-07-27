package com.yourcompany.portfoliogenerator.service;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {
    
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    private String displayName;
    
    @Size(max = 100, message = "Profession must not exceed 100 characters")
    private String profession;
    
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
    
    private String bio;
    
    @Size(max = 200, message = "Profile image URL must not exceed 200 characters")
    private String profileImageUrl;
    
    @Size(max = 200, message = "Resume URL must not exceed 200 characters")
    private String resumeUrl;
    
    @Size(max = 100, message = "Phone number must not exceed 100 characters")
    private String phoneNumber;
    
    @Size(max = 200, message = "Website URL must not exceed 200 characters")
    private String website;
    
    @Size(max = 200, message = "LinkedIn URL must not exceed 200 characters")
    private String linkedinUrl;
    
    @Size(max = 200, message = "GitHub URL must not exceed 200 characters")
    private String githubUrl;
    
    @Size(max = 200, message = "Twitter URL must not exceed 200 characters")
    private String twitterUrl;
    
    private String skills;
    
    private String experience;
    
    private String education;
    
    private String certifications;
    
    private Integer yearsOfExperience;
    
    private Boolean availableForHire;
}

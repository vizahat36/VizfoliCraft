package com.yourcompany.portfoliogenerator.publicsite;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.service.UserProfileRequest;
import com.yourcompany.portfoliogenerator.service.UserProfileResponse;
import com.yourcompany.portfoliogenerator.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfileResponse profile = userProfileService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }
    
    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfileResponse profile = userProfileService.createOrUpdateProfile(user, request);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserProfileResponse profile = userProfileService.updateProfile(user, request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = userProfileService.deleteProfile(user);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/sync/{platform}")
    public ResponseEntity<UserProfileResponse> updateSyncStatus(
            @PathVariable String platform,
            @RequestParam boolean synced,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserProfileResponse profile = userProfileService.updateSyncStatus(user, platform, synced);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.service.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GamificationController {
    
    private final GamificationService gamificationService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal User user) {
        Map<String, Object> dashboard = gamificationService.getGamificationDashboard(user);
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<UserStats> getUserStats(@AuthenticationPrincipal User user) {
        UserStats stats = gamificationService.getUserStats(user);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/badges")
    public ResponseEntity<List<UserBadge>> getUserBadges(@AuthenticationPrincipal User user) {
        List<UserBadge> badges = gamificationService.getUserBadges(user);
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/badges/displayed")
    public ResponseEntity<List<UserBadge>> getDisplayedBadges(@AuthenticationPrincipal User user) {
        List<UserBadge> badges = gamificationService.getUserDisplayedBadges(user);
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/badges/all")
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = gamificationService.getAllBadges();
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/badges/category/{category}")
    public ResponseEntity<List<Badge>> getBadgesByCategory(@PathVariable Badge.BadgeCategory category) {
        List<Badge> badges = gamificationService.getBadgesByCategory(category);
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserStats>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<UserStats> leaderboard = gamificationService.getLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
    
    @GetMapping("/leaderboard/streaks")
    public ResponseEntity<List<UserStats>> getStreakLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<UserStats> leaderboard = gamificationService.getStreakLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
    
    @PostMapping("/activity")
    public ResponseEntity<Void> recordActivity(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        String activityType = request.get("activityType");
        if (activityType == null || activityType.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        gamificationService.recordActivity(user, activityType);
        log.info("Activity recorded for user {}: {}", user.getUsername(), activityType);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/badges/{badgeId}/display")
    public ResponseEntity<Void> toggleBadgeDisplay(
            @AuthenticationPrincipal User user,
            @PathVariable Long badgeId,
            @RequestBody Map<String, Boolean> request) {
        
        // This would be implemented to toggle badge visibility
        // For now, just return OK
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/badge-categories")
    public ResponseEntity<Badge.BadgeCategory[]> getBadgeCategories() {
        return ResponseEntity.ok(Badge.BadgeCategory.values());
    }
    
    @GetMapping("/badge-types")
    public ResponseEntity<Badge.BadgeType[]> getBadgeTypes() {
        return ResponseEntity.ok(Badge.BadgeType.values());
    }
    
    @PostMapping("/initialize-badges")
    public ResponseEntity<Void> initializeBadges() {
        gamificationService.initializeDefaultBadges();
        return ResponseEntity.ok().build();
    }
}

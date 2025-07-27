package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        log.info("Fetching admin dashboard statistics");
        AdminDashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        log.info("Searching users with query: {}", query);
        Page<User> users = adminService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable String userId,
            @RequestParam Role role,
            @AuthenticationPrincipal User admin) {
        log.info("Updating user {} role to {}", userId, role);
        adminService.updateUserRole(userId, role, admin);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(
            @PathVariable String userId,
            @AuthenticationPrincipal User admin) {
        log.info("Toggling user {} status", userId);
        adminService.toggleUserStatus(userId, admin);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/activities")
    public ResponseEntity<Page<ActivityLog>> getAllActivities(Pageable pageable) {
        log.info("Fetching all activities with pagination");
        Page<ActivityLog> activities = adminService.getAllActivities(pageable);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/type/{type}")
    public ResponseEntity<Page<ActivityLog>> getActivitiesByType(
            @PathVariable ActivityLog.ActivityType type,
            Pageable pageable) {
        log.info("Fetching activities by type: {}", type);
        Page<ActivityLog> activities = adminService.getActivitiesByType(type, pageable);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/date-range")
    public ResponseEntity<Page<ActivityLog>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Fetching activities by date range: {} to {}", startDate, endDate);
        Page<ActivityLog> activities = adminService.getActivitiesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/deployments")
    public ResponseEntity<Page<DeployedPortfolio>> getAllDeployments(Pageable pageable) {
        log.info("Fetching all deployments with pagination");
        Page<DeployedPortfolio> deployments = adminService.getAllDeployments(pageable);
        return ResponseEntity.ok(deployments);
    }
    
    @GetMapping("/badges")
    public ResponseEntity<Page<Badge>> getAllBadges(Pageable pageable) {
        log.info("Fetching all badges with pagination");
        Page<Badge> badges = adminService.getAllBadges(pageable);
        return ResponseEntity.ok(badges);
    }
    
    @PostMapping("/badges")
    public ResponseEntity<Badge> createBadge(
            @Valid @RequestBody AdminBadgeRequest request,
            @AuthenticationPrincipal User admin) {
        log.info("Creating new badge: {}", request.getName());
        Badge badge = adminService.createBadge(request, admin);
        return ResponseEntity.ok(badge);
    }
    
    @PutMapping("/badges/{badgeId}")
    public ResponseEntity<Badge> updateBadge(
            @PathVariable String badgeId,
            @Valid @RequestBody AdminBadgeRequest request,
            @AuthenticationPrincipal User admin) {
        log.info("Updating badge: {}", badgeId);
        Badge badge = adminService.updateBadge(badgeId, request, admin);
        return ResponseEntity.ok(badge);
    }
    
    @DeleteMapping("/badges/{badgeId}")
    public ResponseEntity<Void> deleteBadge(
            @PathVariable String badgeId,
            @AuthenticationPrincipal User admin) {
        log.info("Deleting badge: {}", badgeId);
        adminService.deleteBadge(badgeId, admin);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserStatsAggregated>> getLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        log.info("Fetching leaderboard with limit: {}", limit);
        List<UserStatsAggregated> leaderboard = adminService.getLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
    
    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsers() {
        log.info("Exporting users to CSV");
        byte[] csvData = adminService.exportUsersCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "users.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
    
    @GetMapping("/export/activities")
    public ResponseEntity<byte[]> exportActivities(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Exporting activities to CSV for date range: {} to {}", startDate, endDate);
        byte[] csvData = adminService.exportActivitiesCsv(startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "activities.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
    
    @GetMapping("/export/deployments")
    public ResponseEntity<byte[]> exportDeployments() {
        log.info("Exporting deployments to CSV");
        byte[] csvData = adminService.exportDeploymentsCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "deployments.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}

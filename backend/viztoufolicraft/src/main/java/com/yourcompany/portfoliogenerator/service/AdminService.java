package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final DeployedPortfolioRepository deployedPortfolioRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserStatsRepository userStatsRepository;
    private final PortfolioTemplateRepository templateRepository;
    private final ResumeTemplateRepository resumeTemplateRepository;
    private final ActivityLogService activityLogService;
    
    public AdminDashboardStats getDashboardStats() {
        log.info("Generating admin dashboard statistics");
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long totalDeployments = deployedPortfolioRepository.countActiveDeployments();
        long totalTemplates = templateRepository.countByIsActiveTrue();
        long totalBadges = badgeRepository.count();
        
        // Recent activity counts
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        
        long recentUsers = userRepository.countByCreatedAtAfter(last7Days);
        long recentDeployments = deployedPortfolioRepository.countByDateRange(last7Days, LocalDateTime.now()).size();
        long recentActivities = activityLogRepository.countByDateRange(last7Days, LocalDateTime.now());
        
        // Top activity types
        Map<String, Long> topActivityTypes = new HashMap<>();
        for (ActivityLog.ActivityType type : ActivityLog.ActivityType.values()) {
            long count = activityLogRepository.countByType(type);
            if (count > 0) {
                topActivityTypes.put(type.name(), count);
            }
        }
        
        // Platform usage stats
        Map<String, Long> platformStats = new HashMap<>();
        for (DeployedPortfolio.DeploymentPlatform platform : DeployedPortfolio.DeploymentPlatform.values()) {
            long count = deployedPortfolioRepository.countByPlatform(platform);
            if (count > 0) {
                platformStats.put(platform.name(), count);
            }
        }
        
        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalDeployments(totalDeployments)
                .totalTemplates(totalTemplates)
                .totalBadges(totalBadges)
                .recentUsers(recentUsers)
                .recentDeployments(recentDeployments)
                .recentActivities(recentActivities)
                .topActivityTypes(topActivityTypes)
                .platformStats(platformStats)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                query, query, query, query, pageable);
    }
    
    public Page<ActivityLog> getAllActivities(Pageable pageable) {
        return activityLogService.getAllActivities(pageable);
    }
    
    public Page<ActivityLog> getActivitiesByType(ActivityLog.ActivityType type, Pageable pageable) {
        return activityLogService.getActivitiesByType(type, pageable);
    }
    
    public Page<ActivityLog> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return activityLogService.getActivitiesByDateRange(startDate, endDate, pageable);
    }
    
    public Page<DeployedPortfolio> getAllDeployments(Pageable pageable) {
        return deployedPortfolioRepository.findAll(pageable);
    }
    
    public Page<Badge> getAllBadges(Pageable pageable) {
        return badgeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Badge createBadge(AdminBadgeRequest request, User admin) {
        Badge badge = Badge.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .category(request.getCategory())
                .pointsRequired(request.getPointsRequired())
                .isActive(request.isActive())
                .createdAt(LocalDateTime.now())
                .build();
        
        badge = badgeRepository.save(badge);
        
        activityLogService.logActivity(admin, ActivityLog.ActivityType.ADMIN_ACTION,
                "Created badge: " + badge.getName(), "badge", badge.getId());
        
        return badge;
    }
    
    public Badge updateBadge(String badgeId, AdminBadgeRequest request, User admin) {
        Optional<Badge> badgeOpt = badgeRepository.findById(badgeId);
        if (badgeOpt.isEmpty()) {
            throw new RuntimeException("Badge not found: " + badgeId);
        }
        
        Badge badge = badgeOpt.get();
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setIconUrl(request.getIconUrl());
        badge.setCategory(request.getCategory());
        badge.setPointsRequired(request.getPointsRequired());
        badge.setIsActive(request.isActive());
        
        badge = badgeRepository.save(badge);
        
        activityLogService.logActivity(admin, ActivityLog.ActivityType.ADMIN_ACTION,
                "Updated badge: " + badge.getName(), "badge", badge.getId());
        
        return badge;
    }
    
    public void deleteBadge(String badgeId, User admin) {
        Optional<Badge> badgeOpt = badgeRepository.findById(badgeId);
        if (badgeOpt.isEmpty()) {
            throw new RuntimeException("Badge not found: " + badgeId);
        }
        
        Badge badge = badgeOpt.get();
        badge.setIsActive(false);
        badgeRepository.save(badge);
        
        activityLogService.logActivity(admin, ActivityLog.ActivityType.ADMIN_ACTION,
                "Deleted badge: " + badge.getName(), "badge", badge.getId());
    }
    
    public List<UserStatsAggregated> getLeaderboard(int limit) {
        List<UserStats> allStats = userStatsRepository.findAllByOrderByTotalPointsDesc();
        
        return allStats.stream()
                .limit(limit)
                .map(this::convertToAggregated)
                .toList();
    }
    
    public void updateUserRole(String userId, Role newRole, User admin) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + userId);
        }
        
        User user = userOpt.get();
        Role oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);
        
        activityLogService.logActivity(admin, ActivityLog.ActivityType.ADMIN_ACTION,
                "Updated user role from " + oldRole + " to " + newRole,
                "user", userId, oldRole.toString(), newRole.toString());
    }
    
    public void toggleUserStatus(String userId, User admin) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + userId);
        }
        
        User user = userOpt.get();
        boolean oldStatus = user.isEnabled();
        user.setEnabled(!oldStatus);
        userRepository.save(user);
        
        activityLogService.logActivity(admin, ActivityLog.ActivityType.ADMIN_ACTION,
                "Toggled user status from " + oldStatus + " to " + !oldStatus,
                "user", userId, String.valueOf(oldStatus), String.valueOf(!oldStatus));
    }
    
    public byte[] exportUsersCsv() {
        log.info("Exporting users to CSV");
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Username,Email,First Name,Last Name,Role,Enabled,Created At,Last Login");
            
            // User data
            List<User> users = userRepository.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (User user : users) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escapeCSV(user.getId()),
                        escapeCSV(user.getUsername()),
                        escapeCSV(user.getEmail()),
                        escapeCSV(user.getFirstName()),
                        escapeCSV(user.getLastName()),
                        escapeCSV(user.getRole().toString()),
                        user.isEnabled(),
                        user.getCreatedAt().format(formatter),
                        "N/A" // Last login would need to be tracked separately
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Failed to export users CSV", e);
            throw new RuntimeException("Failed to export users CSV", e);
        }
    }
    
    public byte[] exportActivitiesCsv(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Exporting activities to CSV for date range: {} to {}", startDate, endDate);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,User,Action,Type,Description,Entity Type,Entity ID,IP Address,Timestamp");
            
            // Activity data
            List<ActivityLog> activities = activityLogRepository.findByUserAndDateRange(null, startDate, endDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (ActivityLog activity : activities) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escapeCSV(activity.getId()),
                        activity.getUser() != null ? escapeCSV(activity.getUser().getEmail()) : "System",
                        escapeCSV(activity.getAction()),
                        escapeCSV(activity.getType().toString()),
                        escapeCSV(activity.getDescription()),
                        escapeCSV(activity.getEntityType()),
                        escapeCSV(activity.getEntityId()),
                        escapeCSV(activity.getIpAddress()),
                        activity.getTimestamp().format(formatter)
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Failed to export activities CSV", e);
            throw new RuntimeException("Failed to export activities CSV", e);
        }
    }
    
    public byte[] exportDeploymentsCsv() {
        log.info("Exporting deployments to CSV");
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,User,Template,Title,Public URL,Status,Platform,View Count,Created At,Deployed At");
            
            // Deployment data
            List<DeployedPortfolio> deployments = deployedPortfolioRepository.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (DeployedPortfolio deployment : deployments) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escapeCSV(deployment.getId()),
                        deployment.getUser() != null ? escapeCSV(deployment.getUser().getEmail()) : "Unknown",
                        deployment.getTemplate() != null ? escapeCSV(deployment.getTemplate().getName()) : "Unknown",
                        escapeCSV(deployment.getTitle()),
                        escapeCSV(deployment.getPublicUrl()),
                        escapeCSV(deployment.getStatus().toString()),
                        escapeCSV(deployment.getPlatform().toString()),
                        deployment.getViewCount(),
                        deployment.getCreatedAt().format(formatter),
                        deployment.getDeployedAt() != null ? deployment.getDeployedAt().format(formatter) : "Not deployed"
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Failed to export deployments CSV", e);
            throw new RuntimeException("Failed to export deployments CSV", e);
        }
    }
    
    private UserStatsAggregated convertToAggregated(UserStats stats) {
        return UserStatsAggregated.builder()
                .userId(stats.getUser().getId())
                .username(stats.getUser().getUsername())
                .email(stats.getUser().getEmail())
                .totalPoints(stats.getTotalPoints())
                .currentLevel(stats.getCurrentLevel())
                .portfoliosCreated(stats.getPortfoliosCreated())
                .resumesGenerated(stats.getResumesGenerated())
                .profileCompletionPercentage(stats.getProfileCompletionPercentage())
                .badgeCount(userBadgeRepository.countByUser(stats.getUser()))
                .build();
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

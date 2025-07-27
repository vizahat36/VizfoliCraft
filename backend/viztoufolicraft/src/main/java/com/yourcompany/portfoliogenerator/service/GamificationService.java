package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {
    
    private final UserStatsRepository userStatsRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserProfileRepository userProfileRepository;
    private final GeneratedResumeRepository generatedResumeRepository;
    
    // Points configuration
    private static final Map<String, Integer> ACTIVITY_POINTS = Map.of(
        "PROFILE_CREATED", 50,
        "PROFILE_COMPLETED", 100,
        "SOCIAL_CONNECTED", 25,
        "RESUME_GENERATED", 30,
        "PORTFOLIO_DEPLOYED", 75,
        "DAILY_LOGIN", 5,
        "WEEKLY_STREAK", 50,
        "MONTHLY_STREAK", 200
    );
    
    @Transactional
    public UserStats getUserStats(User user) {
        return userStatsRepository.findByUser(user)
                .orElseGet(() -> createInitialUserStats(user));
    }
    
    @Transactional
    public UserStats createInitialUserStats(User user) {
        UserStats stats = UserStats.builder()
                .user(user)
                .totalPoints(0)
                .level(1)
                .experiencePoints(0)
                .pointsToNextLevel(100)
                .portfoliosCreated(0)
                .resumesGenerated(0)
                .profileCompletionPercentage(calculateProfileCompletion(user))
                .socialAccountsConnected(0)
                .badgesEarned(0)
                .currentStreak(0)
                .longestStreak(0)
                .build();
        
        return userStatsRepository.save(stats);
    }
    
    @Transactional
    public void recordActivity(User user, String activityType) {
        UserStats stats = getUserStats(user);
        Integer points = ACTIVITY_POINTS.getOrDefault(activityType, 10);
        
        stats.addPoints(points);
        stats.updateActivity();
        
        // Update specific counters
        switch (activityType) {
            case "RESUME_GENERATED" -> stats.setResumesGenerated(stats.getResumesGenerated() + 1);
            case "PORTFOLIO_DEPLOYED" -> stats.setPortfoliosCreated(stats.getPortfoliosCreated() + 1);
            case "SOCIAL_CONNECTED" -> {
                stats.setSocialAccountsConnected(calculateSocialConnections(user));
                stats.setProfileCompletionPercentage(calculateProfileCompletion(user));
            }
            case "PROFILE_COMPLETED" -> stats.setProfileCompletionPercentage(100);
        }
        
        userStatsRepository.save(stats);
        
        // Check for new badges
        checkAndAwardBadges(user, stats, activityType);
        
        // Update leaderboard position
        updateLeaderboardPosition(user, stats);
        
        log.info("Activity recorded for user {}: {} (+{} points)", user.getUsername(), activityType, points);
    }
    
    @Transactional
    public void checkAndAwardBadges(User user, UserStats stats, String activityType) {
        List<Badge> potentialBadges = new ArrayList<>();
        
        // Check milestone badges
        if ("PROFILE_COMPLETED".equals(activityType)) {
            badgeRepository.findByNameAndIsActiveTrue("Profile Master")
                    .ifPresent(potentialBadges::add);
        }
        
        if ("RESUME_GENERATED".equals(activityType)) {
            if (stats.getResumesGenerated() == 1) {
                badgeRepository.findByNameAndIsActiveTrue("First Resume")
                        .ifPresent(potentialBadges::add);
            } else if (stats.getResumesGenerated() == 10) {
                badgeRepository.findByNameAndIsActiveTrue("Resume Expert")
                        .ifPresent(potentialBadges::add);
            }
        }
        
        // Check level-based badges
        if (stats.getLevel() == 5) {
            badgeRepository.findByNameAndIsActiveTrue("Rising Star")
                    .ifPresent(potentialBadges::add);
        } else if (stats.getLevel() == 10) {
            badgeRepository.findByNameAndIsActiveTrue("Portfolio Pro")
                    .ifPresent(potentialBadges::add);
        }
        
        // Check streak badges
        if (stats.getCurrentStreak() == 7) {
            badgeRepository.findByNameAndIsActiveTrue("Week Warrior")
                    .ifPresent(potentialBadges::add);
        } else if (stats.getCurrentStreak() == 30) {
            badgeRepository.findByNameAndIsActiveTrue("Monthly Master")
                    .ifPresent(potentialBadges::add);
        }
        
        // Check social connection badges
        if (stats.getSocialAccountsConnected() == 2) {
            badgeRepository.findByNameAndIsActiveTrue("Social Connector")
                    .ifPresent(potentialBadges::add);
        }
        
        // Award new badges
        for (Badge badge : potentialBadges) {
            awardBadge(user, badge, activityType);
        }
    }
    
    @Transactional
    public UserBadge awardBadge(User user, Badge badge, String reason) {
        // Check if user already has this badge
        if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
            log.debug("User {} already has badge {}", user.getUsername(), badge.getName());
            return null;
        }
        
        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .earnedPoints(badge.getPointsRequired() != null ? badge.getPointsRequired() : 0)
                .achievementDetails(reason)
                .notificationSent(false)
                .isDisplayed(true)
                .build();
        
        userBadge = userBadgeRepository.save(userBadge);
        
        // Update user stats
        UserStats stats = getUserStats(user);
        stats.setBadgesEarned(stats.getBadgesEarned() + 1);
        userStatsRepository.save(stats);
        
        log.info("Badge awarded to user {}: {} ({})", user.getUsername(), badge.getName(), reason);
        return userBadge;
    }
    
    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUserOrderByEarnedAtDesc(user);
    }
    
    public List<UserBadge> getUserDisplayedBadges(User user) {
        return userBadgeRepository.findByUserAndIsDisplayedTrueOrderByEarnedAtDesc(user);
    }
    
    public List<Badge> getAllBadges() {
        return badgeRepository.findByIsActiveTrueOrderByRarityLevelDescCreatedAtAsc();
    }
    
    public List<Badge> getBadgesByCategory(Badge.BadgeCategory category) {
        return badgeRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public List<UserStats> getLeaderboard(int limit) {
        return userStatsRepository.findAllOrderByTotalPointsDesc()
                .stream()
                .limit(limit)
                .toList();
    }
    
    public List<UserStats> getStreakLeaderboard(int limit) {
        return userStatsRepository.findTopStreaks(limit);
    }
    
    @Transactional
    public void updateLeaderboardPosition(User user, UserStats stats) {
        Long rank = userStatsRepository.countUsersWithMorePoints(stats.getTotalPoints()) + 1;
        stats.setRankPosition(rank.intValue());
        userStatsRepository.save(stats);
    }
    
    public Map<String, Object> getGamificationDashboard(User user) {
        UserStats stats = getUserStats(user);
        List<UserBadge> recentBadges = userBadgeRepository.findByUserOrderByEarnedAtDesc(user)
                .stream()
                .limit(5)
                .toList();
        
        List<UserStats> leaderboard = getLeaderboard(10);
        Long totalUsers = userStatsRepository.count();
        
        return Map.of(
            "userStats", stats,
            "recentBadges", recentBadges,
            "leaderboard", leaderboard,
            "userRank", stats.getRankPosition() != null ? stats.getRankPosition() : totalUsers.intValue(),
            "totalUsers", totalUsers,
            "progressToNextLevel", calculateLevelProgress(stats),
            "availableBadges", getAllBadges().size(),
            "earnedBadges", stats.getBadgesEarned()
        );
    }
    
    private Integer calculateProfileCompletion(User user) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            return 0;
        }
        
        UserProfile profile = profileOpt.get();
        int totalFields = 12; // Total number of profile fields
        int completedFields = 0;
        
        if (profile.getDisplayName() != null && !profile.getDisplayName().trim().isEmpty()) completedFields++;
        if (profile.getBio() != null && !profile.getBio().trim().isEmpty()) completedFields++;
        if (profile.getProfession() != null && !profile.getProfession().trim().isEmpty()) completedFields++;
        if (profile.getLocation() != null && !profile.getLocation().trim().isEmpty()) completedFields++;
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().trim().isEmpty()) completedFields++;
        if (profile.getPhoneNumber() != null && !profile.getPhoneNumber().trim().isEmpty()) completedFields++;
        if (profile.getWebsite() != null && !profile.getWebsite().trim().isEmpty()) completedFields++;
        if (profile.getSkills() != null && !profile.getSkills().trim().isEmpty()) completedFields++;
        if (profile.getExperience() != null && !profile.getExperience().trim().isEmpty()) completedFields++;
        if (profile.getEducation() != null && !profile.getEducation().trim().isEmpty()) completedFields++;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().trim().isEmpty()) completedFields++;
        if (profile.getGithubUrl() != null && !profile.getGithubUrl().trim().isEmpty()) completedFields++;
        
        return (int) ((double) completedFields / totalFields * 100);
    }
    
    private Integer calculateSocialConnections(User user) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            return 0;
        }
        
        UserProfile profile = profileOpt.get();
        int connections = 0;
        
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().trim().isEmpty()) connections++;
        if (profile.getGithubUrl() != null && !profile.getGithubUrl().trim().isEmpty()) connections++;
        if (profile.getTwitterUrl() != null && !profile.getTwitterUrl().trim().isEmpty()) connections++;
        
        return connections;
    }
    
    private Double calculateLevelProgress(UserStats stats) {
        if (stats.getPointsToNextLevel() == 0) {
            return 100.0;
        }
        return (double) stats.getExperiencePoints() / stats.getPointsToNextLevel() * 100;
    }
    
    @Transactional
    public void initializeDefaultBadges() {
        if (badgeRepository.count() > 0) {
            return; // Badges already initialized
        }
        
        List<Badge> defaultBadges = List.of(
            Badge.builder()
                    .name("First Steps")
                    .description("Created your first portfolio profile")
                    .badgeType(Badge.BadgeType.MILESTONE)
                    .category(Badge.BadgeCategory.PROFILE_COMPLETION)
                    .pointsRequired(0)
                    .rarityLevel(Badge.RarityLevel.COMMON)
                    .colorCode("#28a745")
                    .build(),
            
            Badge.builder()
                    .name("Profile Master")
                    .description("Completed 100% of your profile")
                    .badgeType(Badge.BadgeType.ACHIEVEMENT)
                    .category(Badge.BadgeCategory.PROFILE_COMPLETION)
                    .pointsRequired(100)
                    .rarityLevel(Badge.RarityLevel.UNCOMMON)
                    .colorCode("#007bff")
                    .build(),
            
            Badge.builder()
                    .name("First Resume")
                    .description("Generated your first resume")
                    .badgeType(Badge.BadgeType.MILESTONE)
                    .category(Badge.BadgeCategory.RESUME_GENERATION)
                    .pointsRequired(30)
                    .rarityLevel(Badge.RarityLevel.COMMON)
                    .colorCode("#17a2b8")
                    .build(),
            
            Badge.builder()
                    .name("Resume Expert")
                    .description("Generated 10 resumes")
                    .badgeType(Badge.BadgeType.ACHIEVEMENT)
                    .category(Badge.BadgeCategory.RESUME_GENERATION)
                    .pointsRequired(300)
                    .rarityLevel(Badge.RarityLevel.RARE)
                    .colorCode("#dc3545")
                    .build(),
            
            Badge.builder()
                    .name("Social Connector")
                    .description("Connected 2 social media accounts")
                    .badgeType(Badge.BadgeType.ACHIEVEMENT)
                    .category(Badge.BadgeCategory.SOCIAL_INTEGRATION)
                    .pointsRequired(50)
                    .rarityLevel(Badge.RarityLevel.UNCOMMON)
                    .colorCode("#6f42c1")
                    .build(),
            
            Badge.builder()
                    .name("Week Warrior")
                    .description("Maintained a 7-day activity streak")
                    .badgeType(Badge.BadgeType.STREAK)
                    .category(Badge.BadgeCategory.COMMUNITY_ENGAGEMENT)
                    .pointsRequired(35)
                    .rarityLevel(Badge.RarityLevel.UNCOMMON)
                    .colorCode("#fd7e14")
                    .build(),
            
            Badge.builder()
                    .name("Monthly Master")
                    .description("Maintained a 30-day activity streak")
                    .badgeType(Badge.BadgeType.STREAK)
                    .category(Badge.BadgeCategory.COMMUNITY_ENGAGEMENT)
                    .pointsRequired(150)
                    .rarityLevel(Badge.RarityLevel.EPIC)
                    .colorCode("#e83e8c")
                    .build(),
            
            Badge.builder()
                    .name("Rising Star")
                    .description("Reached level 5")
                    .badgeType(Badge.BadgeType.MILESTONE)
                    .category(Badge.BadgeCategory.SKILL_DEVELOPMENT)
                    .pointsRequired(500)
                    .rarityLevel(Badge.RarityLevel.RARE)
                    .colorCode("#ffc107")
                    .build(),
            
            Badge.builder()
                    .name("Portfolio Pro")
                    .description("Reached level 10")
                    .badgeType(Badge.BadgeType.MILESTONE)
                    .category(Badge.BadgeCategory.SKILL_DEVELOPMENT)
                    .pointsRequired(1000)
                    .rarityLevel(Badge.RarityLevel.EPIC)
                    .colorCode("#6610f2")
                    .build()
        );
        
        badgeRepository.saveAll(defaultBadges);
        log.info("Initialized {} default badges", defaultBadges.size());
    }
}

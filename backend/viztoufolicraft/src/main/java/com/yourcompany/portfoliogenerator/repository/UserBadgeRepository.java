package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserBadge;
import com.yourcompany.portfoliogenerator.model.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends MongoRepository<UserBadge, String> {
    
    List<UserBadge> findByUserOrderByEarnedAtDesc(User user);
    
    List<UserBadge> findByUserAndIsDisplayedTrueOrderByEarnedAtDesc(User user);
    
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    
    boolean existsByUserAndBadge(User user, Badge badge);
    
    Long countByUser(User user);
    Long countByUser(@Param("user") User user);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user = :user AND ub.badge.category = :category")
    List<UserBadge> findByUserAndBadgeCategory(@Param("user") User user, @Param("category") Badge.BadgeCategory category);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user = :user AND ub.badge.rarityLevel = :rarity")
    List<UserBadge> findByUserAndRarityLevel(@Param("user") User user, @Param("rarity") Badge.RarityLevel rarity);
    
    @Query("SELECT SUM(ub.earnedPoints) FROM UserBadge ub WHERE ub.user = :user")
    Integer getTotalPointsFromBadges(@Param("user") User user);
}

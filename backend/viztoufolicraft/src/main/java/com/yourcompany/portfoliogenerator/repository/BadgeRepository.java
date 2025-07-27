package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.Badge;
import com.yourcompany.portfoliogenerator.model.BadgeCategory;
import com.yourcompany.portfoliogenerator.model.BadgeType;
import com.yourcompany.portfoliogenerator.model.RarityLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends MongoRepository<Badge, String> {
    
    List<Badge> findByIsActiveTrueOrderByRarityLevelDescCreatedAtAsc();
    
    List<Badge> findByBadgeTypeAndIsActiveTrue(BadgeType badgeType);
    
    List<Badge> findByCategoryAndIsActiveTrue(BadgeCategory category);
    
    List<Badge> findByRarityLevelAndIsActiveTrue(RarityLevel rarityLevel);
    
    Optional<Badge> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT b FROM Badge b WHERE b.pointsRequired <= :points AND b.isActive = true")
    List<Badge> findBadgesEligibleForPoints(Integer points);
    
    @Query("SELECT COUNT(b) FROM Badge b WHERE b.isActive = true")
    Long countActiveBadges();
}

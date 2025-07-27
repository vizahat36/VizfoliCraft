package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "user_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private Integer totalPoints = 0;
    
    private Integer level = 1;
    
    private Integer experiencePoints = 0;
    
    private Integer pointsToNextLevel = 100;
    
    private Integer portfoliosCreated = 0;
    
    private Integer resumesGenerated = 0;
    
    private Integer profileCompletionPercentage = 0;
    
    private Integer socialAccountsConnected = 0;
    
    private Integer badgesEarned = 0;
    
    private Integer currentStreak = 0;
    
    private Integer longestStreak = 0;
    
    private LocalDateTime lastActivityDate;
    
    private Integer rankPosition;
    
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
    
    public void addPoints(Integer points) {
        this.totalPoints += points;
        this.experiencePoints += points;
        
        // Check for level up
        while (this.experiencePoints >= this.pointsToNextLevel) {
            this.experiencePoints -= this.pointsToNextLevel;
            this.level++;
            this.pointsToNextLevel = calculatePointsForNextLevel(this.level);
        }
    }
    
    private Integer calculatePointsForNextLevel(Integer currentLevel) {
        // Exponential growth: base points * (1.2^level)
        return (int) (100 * Math.pow(1.2, currentLevel - 1));
    }
    
    public void updateActivity() {
        LocalDateTime now = LocalDateTime.now();
        
        if (lastActivityDate != null) {
            // Check if it's a new day
            if (lastActivityDate.toLocalDate().isBefore(now.toLocalDate())) {
                // Check if streak continues (activity yesterday)
                if (lastActivityDate.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
                    currentStreak++;
                    if (currentStreak > longestStreak) {
                        longestStreak = currentStreak;
                    }
                } else {
                    // Streak broken
                    currentStreak = 1;
                }
            }
        } else {
            currentStreak = 1;
            longestStreak = 1;
        }
        
        lastActivityDate = now;
    }
}

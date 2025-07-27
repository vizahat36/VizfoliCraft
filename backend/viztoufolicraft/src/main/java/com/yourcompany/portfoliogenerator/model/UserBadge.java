package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "user_badges")
@CompoundIndex(name = "user_badge_unique", def = "{'user': 1, 'badge': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private Badge badge;
    
    private LocalDateTime earnedAt;
    
    private Boolean notificationSent = false;
    
    private Boolean isDisplayed = true;
    
    private Integer earnedPoints;
    
    private String achievementDetails;
    
    // Lifecycle method for MongoDB
    public void onCreate() {
        if (earnedAt == null) {
            earnedAt = LocalDateTime.now();
        }
    }
}

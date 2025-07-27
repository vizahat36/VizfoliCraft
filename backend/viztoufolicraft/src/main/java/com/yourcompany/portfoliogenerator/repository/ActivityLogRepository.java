package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.ActivityLog;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    
    Page<ActivityLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    
    Page<ActivityLog> findByTypeOrderByTimestampDesc(ActivityLog.ActivityType type, Pageable pageable);
    
    Page<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);
    
    List<ActivityLog> findByUserAndTypeOrderByTimestampDesc(User user, ActivityLog.ActivityType type);
    
    @Query("{ 'timestamp': { $gte: ?0, $lte: ?1 } }")
    Page<ActivityLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("{ 'user.id': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<ActivityLog> findByUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{ 'type': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<ActivityLog> findByTypeAndDateRange(ActivityLog.ActivityType type, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByType(ActivityLog.ActivityType type);
    
    long countByUser(User user);
    
    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    void deleteByTimestampBefore(LocalDateTime date);
}

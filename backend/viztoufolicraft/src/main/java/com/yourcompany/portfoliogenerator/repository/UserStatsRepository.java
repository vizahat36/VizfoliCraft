package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends MongoRepository<UserStats, String> {
    
    Optional<UserStats> findByUser(User user);
    
    List<UserStats> findAllByOrderByTotalPointsDescLevelDesc();
    
    List<UserStats> findTop10ByOrderByTotalPointsDescLevelDesc();
    
    List<UserStats> findByLevel(Integer level);
    
    List<UserStats> findTop10ByOrderByCurrentStreakDesc();
    
    Long countByTotalPointsGreaterThan(Integer points);
    
    // Custom aggregation queries for MongoDB
    @Query(value = "{ }", count = true)
    Long countAllUsers();
    
    // Average calculations will be done in service layer
    List<UserStats> findAllByOrderByTotalPointsDesc();
}

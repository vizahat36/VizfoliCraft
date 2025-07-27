package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.GeneratedResume;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedResumeRepository extends MongoRepository<GeneratedResume, String> {
    
    List<GeneratedResume> findByUserOrderByCreatedAtDesc(User user);
    
    List<GeneratedResume> findByUserAndStatusOrderByCreatedAtDesc(User user, GeneratedResume.GenerationStatus status);
    
    Optional<GeneratedResume> findByIdAndUser(String id, User user);
    
    List<GeneratedResume> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
    
    List<GeneratedResume> findByExpiresAtBefore(LocalDateTime now);
    
    Long countByUser(User user);
    
    // For total downloads, we'll calculate in service layer
    List<GeneratedResume> findByUser(User user);
}

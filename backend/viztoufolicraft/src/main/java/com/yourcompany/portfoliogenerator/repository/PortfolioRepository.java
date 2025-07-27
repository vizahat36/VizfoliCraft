package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.Portfolio;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    
    List<Portfolio> findByUser(User user);
    
    List<Portfolio> findByUserAndIsPublicTrue(User user);
    
    List<Portfolio> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Portfolio> findByUserAndId(User user, String id);
    
    List<Portfolio> findByIsPublicTrueOrderByCreatedAtDesc();
    
    long countByUser(User user);
    
    boolean existsByUserAndSlug(User user, String slug);
}

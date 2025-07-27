package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioTemplateRepository extends MongoRepository<PortfolioTemplate, String> {
    
    List<PortfolioTemplate> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<PortfolioTemplate> findByCategoryAndIsActiveTrue(String category);
    
    List<PortfolioTemplate> findByIsPremiumFalseAndIsActiveTrueOrderByCreatedAtDesc();
    
    List<PortfolioTemplate> findByIsPremiumTrueAndIsActiveTrueOrderByCreatedAtDesc();
    
    Optional<PortfolioTemplate> findByIdAndIsActiveTrue(String id);
    
    @Query("{ 'isActive': true, '$or': [" +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } }" +
           "] }")
    List<PortfolioTemplate> searchTemplates(String keyword);
    
    @Query(value = "{ 'isActive': true }", count = true)
    long countByIsActiveTrue();
    
    @Query("{ 'isActive': true }")
    List<PortfolioTemplate> findMostUsedTemplates(int limit);
    
    @Query(value = "{ 'isActive': true }", fields = "{ 'category': 1 }")
    List<String> findDistinctCategories();
}

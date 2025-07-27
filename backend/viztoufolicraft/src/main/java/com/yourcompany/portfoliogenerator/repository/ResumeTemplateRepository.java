package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.ResumeTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeTemplateRepository extends MongoRepository<ResumeTemplate, String> {
    
    List<ResumeTemplate> findByIsActiveTrueOrderByNameAsc();
    
    List<ResumeTemplate> findByTemplateTypeAndIsActiveTrueOrderByNameAsc(String templateType);
    
    List<ResumeTemplate> findByIsPremiumFalseAndIsActiveTrueOrderByNameAsc();
    
    List<ResumeTemplate> findByOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
}

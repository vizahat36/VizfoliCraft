package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioTemplateRepository extends JpaRepository<PortfolioTemplate, Long> {
    
    List<PortfolioTemplate> findByActiveTrue();
    
    List<PortfolioTemplate> findByActiveTrueAndFeaturedTrue();
    
    List<PortfolioTemplate> findByTemplateTypeAndActiveTrue(String templateType);
    
    List<PortfolioTemplate> findByCreatedBy(User createdBy);
    
    @Query("SELECT DISTINCT p.templateType FROM PortfolioTemplate p WHERE p.active = true")
    List<String> findDistinctTemplateTypes();
    
    @Query("SELECT p FROM PortfolioTemplate p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PortfolioTemplate> searchByKeyword(@Param("keyword") String keyword);
}

package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTemplateRepository extends JpaRepository<UserTemplate, Long> {
    
    List<UserTemplate> findByUser(User user);
    
    List<UserTemplate> findByUserAndDeployed(User user, boolean deployed);
    
    Optional<UserTemplate> findByUserAndTemplate(User user, PortfolioTemplate template);
    
    boolean existsByUserAndTemplate(User user, PortfolioTemplate template);
    
    List<UserTemplate> findByDeployedTrue();
}

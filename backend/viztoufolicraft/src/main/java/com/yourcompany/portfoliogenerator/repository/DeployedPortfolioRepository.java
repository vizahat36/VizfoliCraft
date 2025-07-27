package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.DeployedPortfolio;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeployedPortfolioRepository extends MongoRepository<DeployedPortfolio, String> {
    
    List<DeployedPortfolio> findByUserAndIsActiveTrue(User user);
    
    Optional<DeployedPortfolio> findByUserAndIsActiveTrueAndIsPublicTrue(User user);
    
    Optional<DeployedPortfolio> findByPublicUrl(String publicUrl);
    
    Optional<DeployedPortfolio> findBySubdomain(String subdomain);
    
    Optional<DeployedPortfolio> findByCustomDomain(String customDomain);
    
    List<DeployedPortfolio> findByStatus(DeployedPortfolio.DeploymentStatus status);
    
    List<DeployedPortfolio> findByPlatform(DeployedPortfolio.DeploymentPlatform platform);
    
    Page<DeployedPortfolio> findByIsPublicTrueOrderByDeployedAtDesc(Pageable pageable);
    
    @Query("{ 'user.id': ?0, 'isActive': true }")
    List<DeployedPortfolio> findActivePortfoliosByUserId(String userId);
    
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<DeployedPortfolio> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{ 'isPublic': true, 'isActive': true, 'status': 'DEPLOYED' }")
    Page<DeployedPortfolio> findPublicPortfolios(Pageable pageable);
    
    long countByUser(User user);
    
    long countByStatus(DeployedPortfolio.DeploymentStatus status);
    
    long countByPlatform(DeployedPortfolio.DeploymentPlatform platform);
    
    @Query(value = "{ 'isActive': true, 'status': 'DEPLOYED' }", count = true)
    long countActiveDeployments();
}

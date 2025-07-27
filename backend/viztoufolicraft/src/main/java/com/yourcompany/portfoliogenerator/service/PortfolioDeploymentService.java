package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.repository.DeployedPortfolioRepository;
import com.yourcompany.portfoliogenerator.repository.PortfolioTemplateRepository;
import com.yourcompany.portfoliogenerator.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PortfolioDeploymentService {
    
    private final DeployedPortfolioRepository deployedPortfolioRepository;
    private final PortfolioTemplateRepository templateRepository;
    private final UserProfileRepository userProfileRepository;
    private final ActivityLogService activityLogService;
    private final PortfolioBuilderService portfolioBuilderService;
    
    @Value("${app.deployment.base-url:https://portfolios.vizfolicraft.com}")
    private String baseDeploymentUrl;
    
    @Value("${app.deployment.default-platform:INTERNAL_CDN}")
    private String defaultPlatform;
    
    public List<PortfolioTemplate> listAvailableTemplates() {
        log.info("Fetching all available portfolio templates");
        return templateRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<PortfolioTemplate> listTemplatesByCategory(String category) {
        log.info("Fetching templates by category: {}", category);
        return templateRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public List<PortfolioTemplate> listFreeTemplates() {
        log.info("Fetching free templates");
        return templateRepository.findByIsPremiumFalseAndIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<PortfolioTemplate> listPremiumTemplates() {
        log.info("Fetching premium templates");
        return templateRepository.findByIsPremiumTrueAndIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public Optional<PortfolioTemplate> getTemplateById(String templateId) {
        return templateRepository.findByIdAndIsActiveTrue(templateId);
    }
    
    public DeployedPortfolio selectAndDeployTemplate(User user, String templateId, DeploymentRequest request) {
        log.info("Starting portfolio deployment for user: {} with template: {}", user.getEmail(), templateId);
        
        // Validate template
        Optional<PortfolioTemplate> templateOpt = getTemplateById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found or inactive: " + templateId);
        }
        
        PortfolioTemplate template = templateOpt.get();
        
        // Check if user already has an active deployment
        List<DeployedPortfolio> existingDeployments = deployedPortfolioRepository.findByUserAndIsActiveTrue(user);
        if (!existingDeployments.isEmpty() && !request.isAllowMultiple()) {
            throw new RuntimeException("User already has an active portfolio deployment");
        }
        
        // Generate unique identifiers
        String deploymentId = UUID.randomUUID().toString();
        String subdomain = generateSubdomain(user, request.getSubdomain());
        String publicUrl = generatePublicUrl(subdomain);
        
        // Create deployment record
        DeployedPortfolio deployment = DeployedPortfolio.builder()
                .user(user)
                .template(template)
                .deploymentId(deploymentId)
                .publicUrl(publicUrl)
                .subdomain(subdomain)
                .customDomain(request.getCustomDomain())
                .title(request.getTitle() != null ? request.getTitle() : user.getFirstName() + " " + user.getLastName())
                .description(request.getDescription())
                .status(DeployedPortfolio.DeploymentStatus.PENDING)
                .platform(DeployedPortfolio.DeploymentPlatform.valueOf(defaultPlatform))
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .viewCount(0L)
                .isActive(true)
                .isPublic(request.isPublic())
                .passwordProtected(request.isPasswordProtected())
                .password(request.getPassword())
                .sslEnabled(true)
                .cacheEnabled(true)
                .cacheTTL(3600)
                .customCSS(request.getCustomCSS())
                .customJS(request.getCustomJS())
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
        
        // Save initial deployment
        deployment = deployedPortfolioRepository.save(deployment);
        
        // Log activity
        activityLogService.logActivity(user, ActivityLog.ActivityType.TEMPLATE_SELECTION, 
                "Selected template: " + template.getName(), "template", template.getId());
        
        // Start async deployment process
        startDeploymentProcess(deployment);
        
        return deployment;
    }
    
    private void startDeploymentProcess(DeployedPortfolio deployment) {
        try {
            log.info("Starting deployment process for: {}", deployment.getDeploymentId());
            
            // Update status to building
            deployment.setStatus(DeployedPortfolio.DeploymentStatus.BUILDING);
            deployment.setLastBuildTime(LocalDateTime.now());
            deployedPortfolioRepository.save(deployment);
            
            // Build portfolio content
            String portfolioContent = portfolioBuilderService.buildPortfolio(deployment);
            
            // Update status to deploying
            deployment.setStatus(DeployedPortfolio.DeploymentStatus.DEPLOYING);
            deployedPortfolioRepository.save(deployment);
            
            // Deploy to platform
            String finalUrl = deployToplatform(deployment, portfolioContent);
            
            // Update deployment with success
            deployment.setStatus(DeployedPortfolio.DeploymentStatus.DEPLOYED);
            deployment.setPublicUrl(finalUrl);
            deployment.setDeployedAt(LocalDateTime.now());
            deployment.setBuildVersion("1.0.0");
            deployment.setBuildLog("Deployment successful");
            deployedPortfolioRepository.save(deployment);
            
            // Log successful deployment
            activityLogService.logActivity(deployment.getUser(), ActivityLog.ActivityType.PORTFOLIO_DEPLOYMENT,
                    "Portfolio deployed successfully to: " + finalUrl, "deployment", deployment.getId());
            
            log.info("Portfolio deployment completed successfully: {}", finalUrl);
            
        } catch (Exception e) {
            log.error("Deployment failed for: {}", deployment.getDeploymentId(), e);
            
            deployment.setStatus(DeployedPortfolio.DeploymentStatus.FAILED);
            deployment.setBuildLog("Deployment failed: " + e.getMessage());
            deployedPortfolioRepository.save(deployment);
            
            activityLogService.logActivity(deployment.getUser(), ActivityLog.ActivityType.ERROR,
                    "Portfolio deployment failed: " + e.getMessage(), "deployment", deployment.getId());
        }
    }
    
    private String deployToplatform(DeployedPortfolio deployment, String content) {
        // This would integrate with actual deployment platforms
        // For now, simulate deployment
        log.info("Deploying to platform: {} for deployment: {}", 
                deployment.getPlatform(), deployment.getDeploymentId());
        
        // Simulate deployment delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return deployment.getPublicUrl();
    }
    
    private String generateSubdomain(User user, String requestedSubdomain) {
        if (requestedSubdomain != null && !requestedSubdomain.trim().isEmpty()) {
            // Check if subdomain is available
            Optional<DeployedPortfolio> existing = deployedPortfolioRepository.findBySubdomain(requestedSubdomain);
            if (existing.isEmpty()) {
                return requestedSubdomain.toLowerCase().replaceAll("[^a-z0-9-]", "");
            }
        }
        
        // Generate default subdomain
        String baseSubdomain = (user.getFirstName() + user.getLastName()).toLowerCase()
                .replaceAll("[^a-z0-9]", "");
        
        // Check availability and add suffix if needed
        String subdomain = baseSubdomain;
        int counter = 1;
        while (deployedPortfolioRepository.findBySubdomain(subdomain).isPresent()) {
            subdomain = baseSubdomain + counter;
            counter++;
        }
        
        return subdomain;
    }
    
    private String generatePublicUrl(String subdomain) {
        return baseDeploymentUrl.replace("portfolios", subdomain);
    }
    
    public DeployedPortfolio updateDeployment(User user, String deploymentId, DeploymentUpdateRequest request) {
        Optional<DeployedPortfolio> deploymentOpt = deployedPortfolioRepository.findById(deploymentId);
        if (deploymentOpt.isEmpty() || !deploymentOpt.get().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Deployment not found or access denied");
        }
        
        DeployedPortfolio deployment = deploymentOpt.get();
        
        // Update fields
        if (request.getTitle() != null) deployment.setTitle(request.getTitle());
        if (request.getDescription() != null) deployment.setDescription(request.getDescription());
        if (request.getCustomCSS() != null) deployment.setCustomCSS(request.getCustomCSS());
        if (request.getCustomJS() != null) deployment.setCustomJS(request.getCustomJS());
        if (request.getMetaTitle() != null) deployment.setMetaTitle(request.getMetaTitle());
        if (request.getMetaDescription() != null) deployment.setMetaDescription(request.getMetaDescription());
        if (request.isPublic() != null) deployment.setIsPublic(request.isPublic());
        
        deployment.setLastUpdated(LocalDateTime.now());
        
        // Save and redeploy
        deployment = deployedPortfolioRepository.save(deployment);
        startDeploymentProcess(deployment);
        
        activityLogService.logActivity(user, ActivityLog.ActivityType.PORTFOLIO_UPDATE,
                "Updated portfolio deployment", "deployment", deploymentId);
        
        return deployment;
    }
    
    public void deleteDeployment(User user, String deploymentId) {
        Optional<DeployedPortfolio> deploymentOpt = deployedPortfolioRepository.findById(deploymentId);
        if (deploymentOpt.isEmpty() || !deploymentOpt.get().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Deployment not found or access denied");
        }
        
        DeployedPortfolio deployment = deploymentOpt.get();
        deployment.setIsActive(false);
        deployment.setStatus(DeployedPortfolio.DeploymentStatus.DISABLED);
        deployment.setLastUpdated(LocalDateTime.now());
        
        deployedPortfolioRepository.save(deployment);
        
        activityLogService.logActivity(user, ActivityLog.ActivityType.PORTFOLIO_DELETE,
                "Deleted portfolio deployment", "deployment", deploymentId);
        
        log.info("Portfolio deployment disabled: {}", deploymentId);
    }
    
    public List<DeployedPortfolio> getUserDeployments(User user) {
        return deployedPortfolioRepository.findByUserAndIsActiveTrue(user);
    }
    
    public Optional<DeployedPortfolio> getDeploymentByUrl(String url) {
        return deployedPortfolioRepository.findByPublicUrl(url);
    }
    
    public Page<DeployedPortfolio> getPublicPortfolios(Pageable pageable) {
        return deployedPortfolioRepository.findPublicPortfolios(pageable);
    }
    
    public void incrementViewCount(String deploymentId) {
        Optional<DeployedPortfolio> deploymentOpt = deployedPortfolioRepository.findById(deploymentId);
        if (deploymentOpt.isPresent()) {
            DeployedPortfolio deployment = deploymentOpt.get();
            deployment.setViewCount(deployment.getViewCount() + 1);
            deployment.setLastViewed(LocalDateTime.now());
            deployedPortfolioRepository.save(deployment);
        }
    }
}

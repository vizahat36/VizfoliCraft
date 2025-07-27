package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.DeployedPortfolio;
import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.service.DeploymentRequest;
import com.yourcompany.portfoliogenerator.service.DeploymentUpdateRequest;
import com.yourcompany.portfoliogenerator.service.PortfolioDeploymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Slf4j
public class PortfolioDeploymentController {
    
    private final PortfolioDeploymentService deploymentService;
    
    @GetMapping("/templates")
    public ResponseEntity<List<PortfolioTemplate>> listTemplates() {
        log.info("Fetching all available templates");
        List<PortfolioTemplate> templates = deploymentService.listAvailableTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/category/{category}")
    public ResponseEntity<List<PortfolioTemplate>> listTemplatesByCategory(@PathVariable String category) {
        log.info("Fetching templates for category: {}", category);
        List<PortfolioTemplate> templates = deploymentService.listTemplatesByCategory(category);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/free")
    public ResponseEntity<List<PortfolioTemplate>> listFreeTemplates() {
        log.info("Fetching free templates");
        List<PortfolioTemplate> templates = deploymentService.listFreeTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/premium")
    public ResponseEntity<List<PortfolioTemplate>> listPremiumTemplates() {
        log.info("Fetching premium templates");
        List<PortfolioTemplate> templates = deploymentService.listPremiumTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<PortfolioTemplate> getTemplate(@PathVariable String templateId) {
        log.info("Fetching template: {}", templateId);
        Optional<PortfolioTemplate> template = deploymentService.getTemplateById(templateId);
        return template.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/deploy/{templateId}")
    public ResponseEntity<DeployedPortfolio> deployPortfolio(
            @AuthenticationPrincipal User user,
            @PathVariable String templateId,
            @Valid @RequestBody DeploymentRequest request) {
        
        log.info("Deploying portfolio for user: {} with template: {}", user.getEmail(), templateId);
        
        try {
            DeployedPortfolio deployment = deploymentService.selectAndDeployTemplate(user, templateId, request);
            return ResponseEntity.ok(deployment);
        } catch (Exception e) {
            log.error("Failed to deploy portfolio", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/deploy/{deploymentId}")
    public ResponseEntity<DeployedPortfolio> updateDeployment(
            @AuthenticationPrincipal User user,
            @PathVariable String deploymentId,
            @Valid @RequestBody DeploymentUpdateRequest request) {
        
        log.info("Updating deployment: {} for user: {}", deploymentId, user.getEmail());
        
        try {
            DeployedPortfolio deployment = deploymentService.updateDeployment(user, deploymentId, request);
            return ResponseEntity.ok(deployment);
        } catch (Exception e) {
            log.error("Failed to update deployment", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/deploy/{deploymentId}")
    public ResponseEntity<Void> deleteDeployment(
            @AuthenticationPrincipal User user,
            @PathVariable String deploymentId) {
        
        log.info("Deleting deployment: {} for user: {}", deploymentId, user.getEmail());
        
        try {
            deploymentService.deleteDeployment(user, deploymentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete deployment", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deployments")
    public ResponseEntity<List<DeployedPortfolio>> getUserDeployments(@AuthenticationPrincipal User user) {
        log.info("Fetching deployments for user: {}", user.getEmail());
        List<DeployedPortfolio> deployments = deploymentService.getUserDeployments(user);
        return ResponseEntity.ok(deployments);
    }
    
    @GetMapping("/deployments/{deploymentId}")
    public ResponseEntity<DeployedPortfolio> getDeployment(
            @AuthenticationPrincipal User user,
            @PathVariable String deploymentId) {
        
        log.info("Fetching deployment: {} for user: {}", deploymentId, user.getEmail());
        
        List<DeployedPortfolio> userDeployments = deploymentService.getUserDeployments(user);
        Optional<DeployedPortfolio> deployment = userDeployments.stream()
                .filter(d -> d.getId().equals(deploymentId))
                .findFirst();
        
        return deployment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/public")
    public ResponseEntity<Page<DeployedPortfolio>> getPublicPortfolios(Pageable pageable) {
        log.info("Fetching public portfolios");
        Page<DeployedPortfolio> portfolios = deploymentService.getPublicPortfolios(pageable);
        return ResponseEntity.ok(portfolios);
    }
    
    @PostMapping("/view/{deploymentId}")
    public ResponseEntity<Void> incrementViewCount(@PathVariable String deploymentId) {
        log.debug("Incrementing view count for deployment: {}", deploymentId);
        deploymentService.incrementViewCount(deploymentId);
        return ResponseEntity.ok().build();
    }
}

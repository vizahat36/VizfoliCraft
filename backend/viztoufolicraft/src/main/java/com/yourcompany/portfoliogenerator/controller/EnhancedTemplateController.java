package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.service.EnhancedTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EnhancedTemplateController {
    
    private final EnhancedTemplateService templateService;
    
    // Portfolio Template Endpoints
    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioTemplate>> getAllPortfolioTemplates(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(required = false) String search) {
        
        List<PortfolioTemplate> templates;
        
        if (search != null && !search.trim().isEmpty()) {
            templates = templateService.searchPortfolioTemplates(search);
        } else if (category != null) {
            templates = templateService.getPortfolioTemplatesByCategory(category);
        } else if (isPremium != null) {
            if (isPremium) {
                templates = templateService.getPremiumPortfolioTemplates();
            } else {
                templates = templateService.getFreePortfolioTemplates();
            }
        } else {
            templates = templateService.getAllPortfolioTemplates();
        }
        
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/portfolio/{id}")
    public ResponseEntity<PortfolioTemplate> getPortfolioTemplate(@PathVariable Long id) {
        Optional<PortfolioTemplate> template = templateService.getPortfolioTemplateById(id);
        return template.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/portfolio")
    public ResponseEntity<PortfolioTemplate> createPortfolioTemplate(
            @Valid @RequestBody PortfolioTemplate template) {
        
        PortfolioTemplate createdTemplate = templateService.createPortfolioTemplate(template);
        log.info("Created new portfolio template: {}", createdTemplate.getName());
        return ResponseEntity.ok(createdTemplate);
    }
    
    @PutMapping("/portfolio/{id}")
    public ResponseEntity<PortfolioTemplate> updatePortfolioTemplate(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioTemplate template) {
        
        PortfolioTemplate updatedTemplate = templateService.updatePortfolioTemplate(id, template);
        log.info("Updated portfolio template: {}", updatedTemplate.getName());
        return ResponseEntity.ok(updatedTemplate);
    }
    
    @DeleteMapping("/portfolio/{id}")
    public ResponseEntity<Void> deletePortfolioTemplate(@PathVariable Long id) {
        templateService.deletePortfolioTemplate(id);
        log.info("Deleted portfolio template with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    // Resume Template Endpoints
    @GetMapping("/resume")
    public ResponseEntity<List<ResumeTemplate>> getAllResumeTemplates(
            @RequestParam(required = false) ResumeTemplate.TemplateType templateType,
            @RequestParam(required = false) Boolean isPremium) {
        
        List<ResumeTemplate> templates;
        
        if (templateType != null) {
            templates = templateService.getResumeTemplatesByType(templateType);
        } else if (isPremium != null) {
            if (isPremium) {
                templates = templateService.getPremiumResumeTemplates();
            } else {
                templates = templateService.getFreeResumeTemplates();
            }
        } else {
            templates = templateService.getAllResumeTemplates();
        }
        
        return ResponseEntity.ok(templates);
    }
    
    @PostMapping("/resume")
    public ResponseEntity<ResumeTemplate> createResumeTemplate(
            @Valid @RequestBody ResumeTemplate template) {
        
        ResumeTemplate createdTemplate = templateService.createResumeTemplate(template);
        log.info("Created new resume template: {}", createdTemplate.getName());
        return ResponseEntity.ok(createdTemplate);
    }
    
    @PutMapping("/resume/{id}")
    public ResponseEntity<ResumeTemplate> updateResumeTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ResumeTemplate template) {
        
        ResumeTemplate updatedTemplate = templateService.updateResumeTemplate(id, template);
        log.info("Updated resume template: {}", updatedTemplate.getName());
        return ResponseEntity.ok(updatedTemplate);
    }
    
    @DeleteMapping("/resume/{id}")
    public ResponseEntity<Void> deleteResumeTemplate(@PathVariable Long id) {
        templateService.deleteResumeTemplate(id);
        log.info("Deleted resume template with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    // User Template Selection Endpoints
    @PostMapping("/portfolio/{templateId}/select")
    public ResponseEntity<UserTemplate> selectPortfolioTemplate(
            @AuthenticationPrincipal User user,
            @PathVariable Long templateId,
            @RequestBody(required = false) Map<String, Object> customizations) {
        
        UserTemplate userTemplate = templateService.selectPortfolioTemplate(user, templateId, customizations);
        log.info("User {} selected portfolio template: {}", user.getUsername(), templateId);
        return ResponseEntity.ok(userTemplate);
    }
    
    @GetMapping("/my-selections")
    public ResponseEntity<List<UserTemplate>> getUserSelectedTemplates(@AuthenticationPrincipal User user) {
        List<UserTemplate> userTemplates = templateService.getUserSelectedTemplates(user);
        return ResponseEntity.ok(userTemplates);
    }
    
    @GetMapping("/my-active")
    public ResponseEntity<UserTemplate> getUserActiveTemplate(@AuthenticationPrincipal User user) {
        Optional<UserTemplate> activeTemplate = templateService.getUserActiveTemplate(user);
        return activeTemplate.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    @PutMapping("/user-template/{userTemplateId}/customize")
    public ResponseEntity<UserTemplate> customizeUserTemplate(
            @AuthenticationPrincipal User user,
            @PathVariable Long userTemplateId,
            @RequestBody Map<String, Object> customizations) {
        
        UserTemplate userTemplate = templateService.customizeUserTemplate(user, userTemplateId, customizations);
        log.info("User {} customized template: {}", user.getUsername(), userTemplateId);
        return ResponseEntity.ok(userTemplate);
    }
    
    // Template Analytics Endpoints
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getTemplateAnalytics() {
        Map<String, Object> analytics = templateService.getTemplateAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/my-stats")
    public ResponseEntity<Map<String, Object>> getUserTemplateStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = templateService.getUserTemplateStats(user);
        return ResponseEntity.ok(stats);
    }
    
    // Template Categories and Types
    @GetMapping("/portfolio/categories")
    public ResponseEntity<List<String>> getPortfolioCategories() {
        // This would be implemented to return distinct categories
        List<String> categories = List.of("Professional", "Creative", "Minimalist", "Corporate", "Personal", "Tech");
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/resume/types")
    public ResponseEntity<ResumeTemplate.TemplateType[]> getResumeTemplateTypes() {
        return ResponseEntity.ok(ResumeTemplate.TemplateType.values());
    }
    
    // Template Preview Endpoints
    @GetMapping("/portfolio/{id}/preview")
    public ResponseEntity<Map<String, String>> getPortfolioTemplatePreview(@PathVariable Long id) {
        Optional<PortfolioTemplate> template = templateService.getPortfolioTemplateById(id);
        if (template.isPresent()) {
            Map<String, String> preview = Map.of(
                "html", template.get().getHtmlContent() != null ? template.get().getHtmlContent() : "",
                "css", template.get().getCssContent() != null ? template.get().getCssContent() : "",
                "js", template.get().getJsContent() != null ? template.get().getJsContent() : ""
            );
            return ResponseEntity.ok(preview);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/resume/{id}/preview")
    public ResponseEntity<Map<String, String>> getResumeTemplatePreview(@PathVariable Long id) {
        // This would return the resume template content for preview
        Map<String, String> preview = Map.of(
            "html", "<!-- Resume template HTML -->",
            "css", "/* Resume template CSS */"
        );
        return ResponseEntity.ok(preview);
    }
    
    // Initialization Endpoint
    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeDefaultTemplates() {
        templateService.initializeDefaultTemplates();
        log.info("Initialized default templates");
        return ResponseEntity.ok().build();
    }
    
    // Template Validation Endpoint
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTemplate(@RequestBody Map<String, String> templateData) {
        String html = templateData.get("html");
        String css = templateData.get("css");
        String js = templateData.get("js");
        
        // Basic validation logic
        Map<String, Object> validation = Map.of(
            "isValid", html != null && !html.trim().isEmpty(),
            "errors", List.of(),
            "warnings", List.of(),
            "suggestions", List.of("Consider adding responsive design", "Add SEO meta tags")
        );
        
        return ResponseEntity.ok(validation);
    }
}

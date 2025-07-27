package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedTemplateService {
    
    private final PortfolioTemplateRepository portfolioTemplateRepository;
    private final UserTemplateRepository userTemplateRepository;
    private final ResumeTemplateRepository resumeTemplateRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    
    // Portfolio Template Management
    public List<PortfolioTemplate> getAllPortfolioTemplates() {
        return portfolioTemplateRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<PortfolioTemplate> getPortfolioTemplatesByCategory(String category) {
        return portfolioTemplateRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public List<PortfolioTemplate> getFreePortfolioTemplates() {
        return portfolioTemplateRepository.findByIsPremiumFalseAndIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<PortfolioTemplate> getPremiumPortfolioTemplates() {
        return portfolioTemplateRepository.findByIsPremiumTrueAndIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public Optional<PortfolioTemplate> getPortfolioTemplateById(Long id) {
        return portfolioTemplateRepository.findByIdAndIsActiveTrue(id);
    }
    
    public List<PortfolioTemplate> searchPortfolioTemplates(String searchTerm) {
        return portfolioTemplateRepository.searchTemplates(searchTerm);
    }
    
    @Transactional
    public PortfolioTemplate createPortfolioTemplate(PortfolioTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setIsActive(true);
        
        PortfolioTemplate savedTemplate = portfolioTemplateRepository.save(template);
        log.info("Created new portfolio template: {}", savedTemplate.getName());
        return savedTemplate;
    }
    
    @Transactional
    public PortfolioTemplate updatePortfolioTemplate(Long id, PortfolioTemplate templateUpdates) {
        PortfolioTemplate existingTemplate = portfolioTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        
        // Update fields
        if (templateUpdates.getName() != null) existingTemplate.setName(templateUpdates.getName());
        if (templateUpdates.getDescription() != null) existingTemplate.setDescription(templateUpdates.getDescription());
        if (templateUpdates.getCategory() != null) existingTemplate.setCategory(templateUpdates.getCategory());
        if (templateUpdates.getHtmlContent() != null) existingTemplate.setHtmlContent(templateUpdates.getHtmlContent());
        if (templateUpdates.getCssContent() != null) existingTemplate.setCssContent(templateUpdates.getCssContent());
        if (templateUpdates.getJsContent() != null) existingTemplate.setJsContent(templateUpdates.getJsContent());
        if (templateUpdates.getPreviewImageUrl() != null) existingTemplate.setPreviewImageUrl(templateUpdates.getPreviewImageUrl());
        if (templateUpdates.getIsPremium() != null) existingTemplate.setIsPremium(templateUpdates.getIsPremium());
        
        existingTemplate.setUpdatedAt(LocalDateTime.now());
        
        PortfolioTemplate savedTemplate = portfolioTemplateRepository.save(existingTemplate);
        log.info("Updated portfolio template: {}", savedTemplate.getName());
        return savedTemplate;
    }
    
    @Transactional
    public void deletePortfolioTemplate(Long id) {
        PortfolioTemplate template = portfolioTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        
        template.setIsActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        portfolioTemplateRepository.save(template);
        
        log.info("Soft deleted portfolio template: {}", template.getName());
    }
    
    // User Template Management
    @Transactional
    public UserTemplate selectPortfolioTemplate(User user, Long templateId, Map<String, Object> customizations) {
        PortfolioTemplate template = getPortfolioTemplateById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
        
        // Check if user already has a selection for this template
        Optional<UserTemplate> existingUserTemplate = userTemplateRepository.findByUserAndPortfolioTemplate(user, template);
        
        UserTemplate userTemplate;
        if (existingUserTemplate.isPresent()) {
            userTemplate = existingUserTemplate.get();
            userTemplate.setCustomizations(customizations != null ? customizations.toString() : null);
            userTemplate.setSelectedAt(LocalDateTime.now());
        } else {
            userTemplate = UserTemplate.builder()
                    .user(user)
                    .portfolioTemplate(template)
                    .customizations(customizations != null ? customizations.toString() : null)
                    .isActive(true)
                    .selectedAt(LocalDateTime.now())
                    .build();
        }
        
        UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
        
        // Record gamification activity
        gamificationService.recordActivity(user, "TEMPLATE_SELECTED");
        
        log.info("User {} selected template: {}", user.getUsername(), template.getName());
        return savedUserTemplate;
    }
    
    public List<UserTemplate> getUserSelectedTemplates(User user) {
        return userTemplateRepository.findByUserAndIsActiveTrueOrderBySelectedAtDesc(user);
    }
    
    public Optional<UserTemplate> getUserActiveTemplate(User user) {
        return userTemplateRepository.findByUserAndIsActiveTrueOrderBySelectedAtDesc(user)
                .stream()
                .findFirst();
    }
    
    @Transactional
    public UserTemplate customizeUserTemplate(User user, Long userTemplateId, Map<String, Object> customizations) {
        UserTemplate userTemplate = userTemplateRepository.findByIdAndUser(userTemplateId, user)
                .orElseThrow(() -> new RuntimeException("User template not found: " + userTemplateId));
        
        userTemplate.setCustomizations(customizations.toString());
        userTemplate.setSelectedAt(LocalDateTime.now());
        
        UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
        log.info("User {} customized template: {}", user.getUsername(), userTemplate.getPortfolioTemplate().getName());
        return savedUserTemplate;
    }
    
    // Resume Template Management
    public List<ResumeTemplate> getAllResumeTemplates() {
        return resumeTemplateRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<ResumeTemplate> getResumeTemplatesByType(ResumeTemplate.TemplateType templateType) {
        return resumeTemplateRepository.findByTemplateTypeAndIsActiveTrue(templateType);
    }
    
    public List<ResumeTemplate> getFreeResumeTemplates() {
        return resumeTemplateRepository.findByIsPremiumAndIsActiveTrue(false);
    }
    
    public List<ResumeTemplate> getPremiumResumeTemplates() {
        return resumeTemplateRepository.findByIsPremiumAndIsActiveTrue(true);
    }
    
    @Transactional
    public ResumeTemplate createResumeTemplate(ResumeTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setIsActive(true);
        
        ResumeTemplate savedTemplate = resumeTemplateRepository.save(template);
        log.info("Created new resume template: {}", savedTemplate.getName());
        return savedTemplate;
    }
    
    @Transactional
    public ResumeTemplate updateResumeTemplate(Long id, ResumeTemplate templateUpdates) {
        ResumeTemplate existingTemplate = resumeTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume template not found: " + id));
        
        // Update fields
        if (templateUpdates.getName() != null) existingTemplate.setName(templateUpdates.getName());
        if (templateUpdates.getDescription() != null) existingTemplate.setDescription(templateUpdates.getDescription());
        if (templateUpdates.getTemplateType() != null) existingTemplate.setTemplateType(templateUpdates.getTemplateType());
        if (templateUpdates.getHtmlTemplate() != null) existingTemplate.setHtmlTemplate(templateUpdates.getHtmlTemplate());
        if (templateUpdates.getCssStyles() != null) existingTemplate.setCssStyles(templateUpdates.getCssStyles());
        if (templateUpdates.getPreviewImageUrl() != null) existingTemplate.setPreviewImageUrl(templateUpdates.getPreviewImageUrl());
        if (templateUpdates.getIsPremium() != null) existingTemplate.setIsPremium(templateUpdates.getIsPremium());
        
        existingTemplate.setUpdatedAt(LocalDateTime.now());
        
        ResumeTemplate savedTemplate = resumeTemplateRepository.save(existingTemplate);
        log.info("Updated resume template: {}", savedTemplate.getName());
        return savedTemplate;
    }
    
    @Transactional
    public void deleteResumeTemplate(Long id) {
        ResumeTemplate template = resumeTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume template not found: " + id));
        
        template.setIsActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        resumeTemplateRepository.save(template);
        
        log.info("Soft deleted resume template: {}", template.getName());
    }
    
    // Template Analytics
    public Map<String, Object> getTemplateAnalytics() {
        Long totalPortfolioTemplates = portfolioTemplateRepository.countActiveTemplates();
        Long totalResumeTemplates = resumeTemplateRepository.countActiveTemplates();
        Long totalUserSelections = userTemplateRepository.count();
        
        // Most popular templates
        List<Object[]> popularPortfolioTemplates = portfolioTemplateRepository.findMostUsedTemplates(5);
        
        return Map.of(
            "totalPortfolioTemplates", totalPortfolioTemplates,
            "totalResumeTemplates", totalResumeTemplates,
            "totalUserSelections", totalUserSelections,
            "popularPortfolioTemplates", popularPortfolioTemplates,
            "categories", portfolioTemplateRepository.findDistinctCategories()
        );
    }
    
    public Map<String, Object> getUserTemplateStats(User user) {
        List<UserTemplate> userTemplates = getUserSelectedTemplates(user);
        Optional<UserTemplate> activeTemplate = getUserActiveTemplate(user);
        
        return Map.of(
            "selectedTemplatesCount", userTemplates.size(),
            "activeTemplate", activeTemplate.orElse(null),
            "lastSelectionDate", userTemplates.isEmpty() ? null : userTemplates.get(0).getSelectedAt(),
            "hasActiveTemplate", activeTemplate.isPresent()
        );
    }
    
    @Transactional
    public void initializeDefaultTemplates() {
        if (portfolioTemplateRepository.count() > 0) {
            return; // Templates already initialized
        }
        
        // Create default portfolio templates
        List<PortfolioTemplate> defaultPortfolioTemplates = List.of(
            PortfolioTemplate.builder()
                    .name("Modern Professional")
                    .description("A clean, modern design perfect for professionals")
                    .category("Professional")
                    .htmlContent(getDefaultHtmlContent())
                    .cssContent(getDefaultCssContent())
                    .jsContent(getDefaultJsContent())
                    .isPremium(false)
                    .isActive(true)
                    .build(),
            
            PortfolioTemplate.builder()
                    .name("Creative Designer")
                    .description("A vibrant, creative template for designers and artists")
                    .category("Creative")
                    .htmlContent(getDefaultHtmlContent())
                    .cssContent(getDefaultCssContent())
                    .jsContent(getDefaultJsContent())
                    .isPremium(true)
                    .isActive(true)
                    .build(),
            
            PortfolioTemplate.builder()
                    .name("Minimalist")
                    .description("Clean and simple design focusing on content")
                    .category("Minimalist")
                    .htmlContent(getDefaultHtmlContent())
                    .cssContent(getDefaultCssContent())
                    .jsContent(getDefaultJsContent())
                    .isPremium(false)
                    .isActive(true)
                    .build()
        );
        
        portfolioTemplateRepository.saveAll(defaultPortfolioTemplates);
        log.info("Initialized {} default portfolio templates", defaultPortfolioTemplates.size());
    }
    
    private String getDefaultHtmlContent() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>{{personalInfo.name}} - Portfolio</title>
            </head>
            <body>
                <header class="header">
                    <h1>{{personalInfo.name}}</h1>
                    <p class="title">{{personalInfo.title}}</p>
                </header>
                
                <section class="about">
                    <h2>About Me</h2>
                    <p>{{personalInfo.bio}}</p>
                </section>
                
                <section class="projects">
                    <h2>Projects</h2>
                    <div class="project-grid">
                        {{#each projects}}
                        <div class="project-card">
                            <h3>{{name}}</h3>
                            <p>{{description}}</p>
                            <div class="technologies">
                                {{#each technologies}}
                                <span class="tech">{{this}}</span>
                                {{/each}}
                            </div>
                        </div>
                        {{/each}}
                    </div>
                </section>
                
                <section class="contact">
                    <h2>Contact</h2>
                    <p>Email: {{personalInfo.email}}</p>
                    <div class="social-links">
                        {{#if socialLinks.linkedin}}<a href="{{socialLinks.linkedin}}">LinkedIn</a>{{/if}}
                        {{#if socialLinks.github}}<a href="{{socialLinks.github}}">GitHub</a>{{/if}}
                    </div>
                </section>
            </body>
            </html>
            """;
    }
    
    private String getDefaultCssContent() {
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Arial', sans-serif;
                line-height: 1.6;
                color: #333;
                background-color: #f4f4f4;
            }
            
            .header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                text-align: center;
                padding: 4rem 2rem;
            }
            
            .header h1 {
                font-size: 3rem;
                margin-bottom: 0.5rem;
            }
            
            .title {
                font-size: 1.5rem;
                opacity: 0.9;
            }
            
            section {
                max-width: 1200px;
                margin: 0 auto;
                padding: 3rem 2rem;
            }
            
            h2 {
                font-size: 2rem;
                margin-bottom: 1.5rem;
                color: #2c3e50;
            }
            
            .project-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 2rem;
                margin-top: 2rem;
            }
            
            .project-card {
                background: white;
                padding: 2rem;
                border-radius: 10px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                transition: transform 0.3s ease;
            }
            
            .project-card:hover {
                transform: translateY(-5px);
            }
            
            .technologies {
                margin-top: 1rem;
            }
            
            .tech {
                display: inline-block;
                background: #e74c3c;
                color: white;
                padding: 0.25rem 0.75rem;
                border-radius: 15px;
                font-size: 0.875rem;
                margin: 0.25rem 0.25rem 0 0;
            }
            
            .social-links a {
                display: inline-block;
                margin: 0 1rem 0 0;
                padding: 0.5rem 1rem;
                background: #3498db;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                transition: background 0.3s ease;
            }
            
            .social-links a:hover {
                background: #2980b9;
            }
            
            @media (max-width: 768px) {
                .header h1 {
                    font-size: 2rem;
                }
                
                .title {
                    font-size: 1.25rem;
                }
                
                section {
                    padding: 2rem 1rem;
                }
                
                .project-grid {
                    grid-template-columns: 1fr;
                }
            }
            """;
    }
    
    private String getDefaultJsContent() {
        return """
            // Portfolio JavaScript functionality
            document.addEventListener('DOMContentLoaded', function() {
                // Smooth scrolling for navigation links
                const links = document.querySelectorAll('a[href^="#"]');
                
                links.forEach(link => {
                    link.addEventListener('click', function(e) {
                        e.preventDefault();
                        
                        const targetId = this.getAttribute('href');
                        const targetSection = document.querySelector(targetId);
                        
                        if (targetSection) {
                            targetSection.scrollIntoView({
                                behavior: 'smooth'
                            });
                        }
                    });
                });
                
                // Add animation to project cards on scroll
                const observerOptions = {
                    threshold: 0.1,
                    rootMargin: '0px 0px -50px 0px'
                };
                
                const observer = new IntersectionObserver(function(entries) {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            entry.target.style.opacity = '1';
                            entry.target.style.transform = 'translateY(0)';
                        }
                    });
                }, observerOptions);
                
                // Observe all project cards
                const projectCards = document.querySelectorAll('.project-card');
                projectCards.forEach(card => {
                    card.style.opacity = '0';
                    card.style.transform = 'translateY(20px)';
                    card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    observer.observe(card);
                });
                
                // Add contact form validation if form exists
                const contactForm = document.querySelector('#contact-form');
                if (contactForm) {
                    contactForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        
                        // Basic form validation
                        const email = this.querySelector('input[type="email"]');
                        const message = this.querySelector('textarea');
                        
                        if (!email.value || !message.value) {
                            alert('Please fill in all required fields.');
                            return;
                        }
                        
                        // Here you would typically send the form data to your server
                        alert('Thank you for your message! I will get back to you soon.');
                        this.reset();
                    });
                }
            });
            """;
    }
}

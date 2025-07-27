package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.repository.PortfolioTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {
    
    private final PortfolioTemplateRepository templateRepository;
    
    public List<TemplateResponse> getAllActiveTemplates() {
        return templateRepository.findByActiveTrue()
                .stream()
                .map(TemplateResponse::fromTemplatePreview)
                .collect(Collectors.toList());
    }
    
    public List<TemplateResponse> getFeaturedTemplates() {
        return templateRepository.findByActiveTrueAndFeaturedTrue()
                .stream()
                .map(TemplateResponse::fromTemplatePreview)
                .collect(Collectors.toList());
    }
    
    public List<TemplateResponse> getTemplatesByType(String templateType) {
        return templateRepository.findByTemplateTypeAndActiveTrue(templateType)
                .stream()
                .map(TemplateResponse::fromTemplatePreview)
                .collect(Collectors.toList());
    }
    
    public List<String> getTemplateTypes() {
        return templateRepository.findDistinctTemplateTypes();
    }
    
    public Optional<TemplateResponse> getTemplateById(Long id) {
        return templateRepository.findById(id)
                .filter(PortfolioTemplate::isActive)
                .map(TemplateResponse::fromTemplate);
    }
    
    public List<TemplateResponse> searchTemplates(String keyword) {
        return templateRepository.searchByKeyword(keyword)
                .stream()
                .map(TemplateResponse::fromTemplatePreview)
                .collect(Collectors.toList());
    }
    
    public TemplateResponse createTemplate(TemplateCreateRequest request, User creator) {
        PortfolioTemplate template = new PortfolioTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setTemplateType(request.getTemplateType());
        template.setHtmlContent(request.getHtmlContent());
        template.setCssContent(request.getCssContent());
        template.setJsContent(request.getJsContent());
        template.setJsonConfig(request.getJsonConfig());
        template.setPreviewImageUrl(request.getPreviewImageUrl());
        template.setFeatured(request.isFeatured());
        template.setCreatedBy(creator);
        
        PortfolioTemplate savedTemplate = templateRepository.save(template);
        return TemplateResponse.fromTemplate(savedTemplate);
    }
    
    public Optional<TemplateResponse> updateTemplate(Long id, TemplateCreateRequest request, User user) {
        return templateRepository.findById(id)
                .filter(template -> template.getCreatedBy().getId().equals(user.getId()) || isAdmin(user))
                .map(template -> {
                    template.setName(request.getName());
                    template.setDescription(request.getDescription());
                    template.setTemplateType(request.getTemplateType());
                    template.setHtmlContent(request.getHtmlContent());
                    template.setCssContent(request.getCssContent());
                    template.setJsContent(request.getJsContent());
                    template.setJsonConfig(request.getJsonConfig());
                    template.setPreviewImageUrl(request.getPreviewImageUrl());
                    template.setFeatured(request.isFeatured());
                    
                    PortfolioTemplate savedTemplate = templateRepository.save(template);
                    return TemplateResponse.fromTemplate(savedTemplate);
                });
    }
    
    public boolean deleteTemplate(Long id, User user) {
        return templateRepository.findById(id)
                .filter(template -> template.getCreatedBy().getId().equals(user.getId()) || isAdmin(user))
                .map(template -> {
                    template.setActive(false);
                    templateRepository.save(template);
                    return true;
                })
                .orElse(false);
    }
    
    public List<TemplateResponse> getUserTemplates(User user) {
        return templateRepository.findByCreatedBy(user)
                .stream()
                .map(TemplateResponse::fromTemplate)
                .collect(Collectors.toList());
    }
    
    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ADMIN");
    }
}

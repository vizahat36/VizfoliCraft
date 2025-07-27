package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserTemplate;
import com.yourcompany.portfoliogenerator.repository.PortfolioTemplateRepository;
import com.yourcompany.portfoliogenerator.repository.UserTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTemplateService {
    
    private final UserTemplateRepository userTemplateRepository;
    private final PortfolioTemplateRepository templateRepository;
    
    public List<UserTemplateResponse> getUserTemplates(User user) {
        return userTemplateRepository.findByUser(user)
                .stream()
                .map(UserTemplateResponse::fromUserTemplate)
                .collect(Collectors.toList());
    }
    
    public List<UserTemplateResponse> getUserDeployedTemplates(User user) {
        return userTemplateRepository.findByUserAndDeployed(user, true)
                .stream()
                .map(UserTemplateResponse::fromUserTemplate)
                .collect(Collectors.toList());
    }
    
    public Optional<UserTemplateResponse> selectTemplate(Long templateId, User user) {
        return templateRepository.findById(templateId)
                .filter(PortfolioTemplate::isActive)
                .map(template -> {
                    // Check if user already has this template
                    Optional<UserTemplate> existingUserTemplate = 
                            userTemplateRepository.findByUserAndTemplate(user, template);
                    
                    if (existingUserTemplate.isPresent()) {
                        return UserTemplateResponse.fromUserTemplate(existingUserTemplate.get());
                    }
                    
                    // Create new user template
                    UserTemplate userTemplate = new UserTemplate();
                    userTemplate.setUser(user);
                    userTemplate.setTemplate(template);
                    userTemplate.setCustomizedHtml(template.getHtmlContent());
                    userTemplate.setCustomizedCss(template.getCssContent());
                    userTemplate.setCustomizedJs(template.getJsContent());
                    userTemplate.setUserData(template.getJsonConfig()); // Default config
                    
                    UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
                    return UserTemplateResponse.fromUserTemplate(savedUserTemplate);
                });
    }
    
    public Optional<UserTemplateResponse> updateUserTemplate(Long userTemplateId, 
                                                             UserTemplateRequest request, 
                                                             User user) {
        return userTemplateRepository.findById(userTemplateId)
                .filter(userTemplate -> userTemplate.getUser().getId().equals(user.getId()))
                .map(userTemplate -> {
                    userTemplate.setCustomizedHtml(request.getCustomizedHtml());
                    userTemplate.setCustomizedCss(request.getCustomizedCss());
                    userTemplate.setCustomizedJs(request.getCustomizedJs());
                    userTemplate.setUserData(request.getUserData());
                    
                    UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
                    return UserTemplateResponse.fromUserTemplate(savedUserTemplate);
                });
    }
    
    public Optional<UserTemplateResponse> deployTemplate(Long userTemplateId, User user) {
        return userTemplateRepository.findById(userTemplateId)
                .filter(userTemplate -> userTemplate.getUser().getId().equals(user.getId()))
                .map(userTemplate -> {
                    // Generate a unique deployment URL
                    String deploymentUrl = generateDeploymentUrl(user.getUsername(), userTemplate.getId());
                    
                    userTemplate.setDeployed(true);
                    userTemplate.setDeploymentUrl(deploymentUrl);
                    
                    UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
                    return UserTemplateResponse.fromUserTemplate(savedUserTemplate);
                });
    }
    
    public Optional<UserTemplateResponse> undeployTemplate(Long userTemplateId, User user) {
        return userTemplateRepository.findById(userTemplateId)
                .filter(userTemplate -> userTemplate.getUser().getId().equals(user.getId()))
                .map(userTemplate -> {
                    userTemplate.setDeployed(false);
                    userTemplate.setDeploymentUrl(null);
                    
                    UserTemplate savedUserTemplate = userTemplateRepository.save(userTemplate);
                    return UserTemplateResponse.fromUserTemplate(savedUserTemplate);
                });
    }
    
    public boolean deleteUserTemplate(Long userTemplateId, User user) {
        return userTemplateRepository.findById(userTemplateId)
                .filter(userTemplate -> userTemplate.getUser().getId().equals(user.getId()))
                .map(userTemplate -> {
                    userTemplateRepository.delete(userTemplate);
                    return true;
                })
                .orElse(false);
    }
    
    public Optional<UserTemplateResponse> getUserTemplateById(Long userTemplateId, User user) {
        return userTemplateRepository.findById(userTemplateId)
                .filter(userTemplate -> userTemplate.getUser().getId().equals(user.getId()))
                .map(UserTemplateResponse::fromUserTemplate);
    }
    
    public Optional<UserTemplate> getDeployedTemplateByUrl(String deploymentUrl) {
        return userTemplateRepository.findAll()
                .stream()
                .filter(userTemplate -> userTemplate.isDeployed() && 
                                       deploymentUrl.equals(userTemplate.getDeploymentUrl()))
                .findFirst();
    }
    
    private String generateDeploymentUrl(String username, Long templateId) {
        return String.format("/portfolio/%s/%s", username, UUID.randomUUID().toString().substring(0, 8));
    }
}

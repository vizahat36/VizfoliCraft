package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.DeployedPortfolio;
import com.yourcompany.portfoliogenerator.model.PortfolioTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserProfile;
import com.yourcompany.portfoliogenerator.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioBuilderService {
    
    private final UserProfileRepository userProfileRepository;
    private final TemplateEngine templateEngine;
    
    public String buildPortfolio(DeployedPortfolio deployment) {
        log.info("Building portfolio for deployment: {}", deployment.getDeploymentId());
        
        User user = deployment.getUser();
        PortfolioTemplate template = deployment.getTemplate();
        
        // Get user profile data
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        
        // Create template context
        Context context = createTemplateContext(user, userProfile, deployment);
        
        // Process template
        String htmlContent = processTemplate(template, context);
        
        // Apply customizations
        htmlContent = applyCustomizations(htmlContent, deployment);
        
        log.info("Portfolio built successfully for: {}", deployment.getDeploymentId());
        return htmlContent;
    }
    
    private Context createTemplateContext(User user, UserProfile userProfile, DeployedPortfolio deployment) {
        Context context = new Context();
        
        // User information
        context.setVariable("user", user);
        context.setVariable("profile", userProfile);
        context.setVariable("deployment", deployment);
        
        // SEO and meta information
        context.setVariable("title", deployment.getTitle());
        context.setVariable("description", deployment.getDescription());
        context.setVariable("metaTitle", deployment.getMetaTitle());
        context.setVariable("metaDescription", deployment.getMetaDescription());
        context.setVariable("metaKeywords", deployment.getMetaKeywords());
        
        // Portfolio data
        Map<String, Object> portfolioData = new HashMap<>();
        portfolioData.put("fullName", user.getFirstName() + " " + user.getLastName());
        portfolioData.put("email", user.getEmail());
        
        if (userProfile != null) {
            portfolioData.put("profession", userProfile.getProfession());
            portfolioData.put("bio", userProfile.getBio());
            portfolioData.put("location", userProfile.getLocation());
            portfolioData.put("phone", userProfile.getPhoneNumber());
            portfolioData.put("website", userProfile.getWebsite());
            portfolioData.put("linkedin", userProfile.getLinkedinUrl());
            portfolioData.put("github", userProfile.getGithubUrl());
            portfolioData.put("twitter", userProfile.getTwitterUrl());
            portfolioData.put("skills", parseSkills(userProfile.getSkills()));
            portfolioData.put("experience", parseExperience(userProfile.getExperience()));
            portfolioData.put("education", parseEducation(userProfile.getEducation()));
            portfolioData.put("certifications", parseCertifications(userProfile.getCertifications()));
            portfolioData.put("profileImage", userProfile.getProfileImageUrl());
            portfolioData.put("yearsOfExperience", userProfile.getYearsOfExperience());
            portfolioData.put("availableForHire", userProfile.getAvailableForHire());
        }
        
        context.setVariable("portfolio", portfolioData);
        
        // Current timestamp for cache busting
        context.setVariable("timestamp", System.currentTimeMillis());
        
        return context;
    }
    
    private String processTemplate(PortfolioTemplate template, Context context) {
        try {
            // Create a template from the stored HTML content
            String templateContent = template.getHtmlContent();
            
            // Process with Thymeleaf if it contains Thymeleaf syntax
            if (templateContent.contains("th:")) {
                return templateEngine.process(templateContent, context);
            } else {
                // Simple variable replacement for non-Thymeleaf templates
                return replaceVariables(templateContent, context);
            }
            
        } catch (Exception e) {
            log.error("Error processing template: {}", template.getId(), e);
            return generateFallbackPortfolio(context);
        }
    }
    
    private String replaceVariables(String template, Context context) {
        String result = template;
        
        // Replace common variables
        Map<String, Object> variables = context.getVariables();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value != null) {
                String placeholder = "{{" + key + "}}";
                result = result.replace(placeholder, value.toString());
            }
        }
        
        return result;
    }
    
    private String applyCustomizations(String htmlContent, DeployedPortfolio deployment) {
        StringBuilder result = new StringBuilder(htmlContent);
        
        // Add custom CSS
        if (deployment.getCustomCSS() != null && !deployment.getCustomCSS().trim().isEmpty()) {
            String customCss = "<style type=\"text/css\">\n" + deployment.getCustomCSS() + "\n</style>";
            int headCloseIndex = result.indexOf("</head>");
            if (headCloseIndex != -1) {
                result.insert(headCloseIndex, customCss);
            } else {
                result.insert(0, customCss);
            }
        }
        
        // Add custom JavaScript
        if (deployment.getCustomJS() != null && !deployment.getCustomJS().trim().isEmpty()) {
            String customJs = "<script type=\"text/javascript\">\n" + deployment.getCustomJS() + "\n</script>";
            int bodyCloseIndex = result.lastIndexOf("</body>");
            if (bodyCloseIndex != -1) {
                result.insert(bodyCloseIndex, customJs);
            } else {
                result.append(customJs);
            }
        }
        
        // Add analytics tracking if configured
        if (deployment.getAnalyticsId() != null) {
            String analytics = generateAnalyticsCode(deployment.getAnalyticsId());
            int bodyCloseIndex = result.lastIndexOf("</body>");
            if (bodyCloseIndex != -1) {
                result.insert(bodyCloseIndex, analytics);
            }
        }
        
        return result.toString();
    }
    
    private String generateAnalyticsCode(String analyticsId) {
        return String.format("""
            <!-- Analytics -->
            <script async src="https://www.googletagmanager.com/gtag/js?id=%s"></script>
            <script>
              window.dataLayer = window.dataLayer || [];
              function gtag(){dataLayer.push(arguments);}
              gtag('js', new Date());
              gtag('config', '%s');
            </script>
            """, analyticsId, analyticsId);
    }
    
    private String generateFallbackPortfolio(Context context) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Portfolio</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body>
                <h1>Portfolio</h1>
                <p>This is a fallback portfolio template.</p>
            </body>
            </html>
            """;
    }
    
    private String[] parseSkills(String skills) {
        if (skills == null || skills.trim().isEmpty()) {
            return new String[0];
        }
        return skills.split(",\\s*");
    }
    
    private String parseExperience(String experience) {
        return experience != null ? experience : "";
    }
    
    private String parseEducation(String education) {
        return education != null ? education : "";
    }
    
    private String parseCertifications(String certifications) {
        return certifications != null ? certifications : "";
    }
}

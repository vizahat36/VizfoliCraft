package com.yourcompany.portfoliogenerator.publicsite;

import com.yourcompany.portfoliogenerator.model.UserTemplate;
import com.yourcompany.portfoliogenerator.template.UserTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioRenderController {
    
    private final UserTemplateService userTemplateService;
    
    @GetMapping("/{username}/{portfolioId}")
    public ResponseEntity<String> renderPortfolio(
            @PathVariable String username,
            @PathVariable String portfolioId) {
        
        String deploymentUrl = String.format("/portfolio/%s/%s", username, portfolioId);
        
        return userTemplateService.getDeployedTemplateByUrl(deploymentUrl)
                .map(this::generatePortfolioHtml)
                .map(html -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{username}/{portfolioId}/preview")
    public ResponseEntity<String> previewPortfolio(
            @PathVariable String username,
            @PathVariable String portfolioId) {
        
        String deploymentUrl = String.format("/portfolio/%s/%s", username, portfolioId);
        
        return userTemplateService.getDeployedTemplateByUrl(deploymentUrl)
                .map(this::generatePortfolioHtml)
                .map(html -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html))
                .orElse(ResponseEntity.notFound().build());
    }
    
    private String generatePortfolioHtml(UserTemplate userTemplate) {
        String html = userTemplate.getCustomizedHtml();
        String css = userTemplate.getCustomizedCss();
        String js = userTemplate.getCustomizedJs();
        String userData = userTemplate.getUserData();
        
        // Create a complete HTML document
        StringBuilder fullHtml = new StringBuilder();
        fullHtml.append("<!DOCTYPE html>\n");
        fullHtml.append("<html lang=\"en\">\n");
        fullHtml.append("<head>\n");
        fullHtml.append("    <meta charset=\"UTF-8\">\n");
        fullHtml.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        fullHtml.append("    <title>").append(userTemplate.getUser().getFirstName())
                .append(" ").append(userTemplate.getUser().getLastName()).append(" - Portfolio</title>\n");
        
        // Add CSS
        if (css != null && !css.trim().isEmpty()) {
            fullHtml.append("    <style>\n");
            fullHtml.append(css);
            fullHtml.append("\n    </style>\n");
        }
        
        fullHtml.append("</head>\n");
        fullHtml.append("<body>\n");
        
        // Add HTML content
        if (html != null && !html.trim().isEmpty()) {
            // Replace placeholders with user data if needed
            String processedHtml = processHtmlWithUserData(html, userData);
            fullHtml.append(processedHtml);
        }
        
        // Add JavaScript
        if (js != null && !js.trim().isEmpty()) {
            fullHtml.append("\n    <script>\n");
            fullHtml.append(js);
            fullHtml.append("\n    </script>\n");
        }
        
        fullHtml.append("</body>\n");
        fullHtml.append("</html>");
        
        return fullHtml.toString();
    }
    
    private String processHtmlWithUserData(String html, String userData) {
        // Simple placeholder replacement
        // In a real implementation, you might use a more sophisticated template engine
        if (userData != null && !userData.trim().isEmpty()) {
            // Replace common placeholders
            html = html.replaceAll("\\{\\{USER_DATA\\}\\}", userData);
        }
        return html;
    }
}

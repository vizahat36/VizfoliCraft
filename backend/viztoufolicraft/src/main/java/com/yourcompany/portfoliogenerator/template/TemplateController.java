package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TemplateController {
    
    private final TemplateService templateService;
    
    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        List<TemplateResponse> templates = templateService.getAllActiveTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<TemplateResponse>> getFeaturedTemplates() {
        List<TemplateResponse> templates = templateService.getFeaturedTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/types")
    public ResponseEntity<List<String>> getTemplateTypes() {
        List<String> types = templateService.getTemplateTypes();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/type/{templateType}")
    public ResponseEntity<List<TemplateResponse>> getTemplatesByType(@PathVariable String templateType) {
        List<TemplateResponse> templates = templateService.getTemplatesByType(templateType);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TemplateResponse>> searchTemplates(@RequestParam String keyword) {
        List<TemplateResponse> templates = templateService.searchTemplates(keyword);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(
            @Valid @RequestBody TemplateCreateRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        TemplateResponse response = templateService.createTemplate(request, user);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateCreateRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return templateService.updateTemplate(id, request, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = templateService.deleteTemplate(id, user);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/my-templates")
    public ResponseEntity<List<TemplateResponse>> getUserTemplates(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<TemplateResponse> templates = templateService.getUserTemplates(user);
        return ResponseEntity.ok(templates);
    }
}

package com.yourcompany.portfoliogenerator.template;

import com.yourcompany.portfoliogenerator.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserTemplateController {
    
    private final UserTemplateService userTemplateService;
    
    @GetMapping
    public ResponseEntity<List<UserTemplateResponse>> getUserTemplates(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<UserTemplateResponse> templates = userTemplateService.getUserTemplates(user);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/deployed")
    public ResponseEntity<List<UserTemplateResponse>> getDeployedTemplates(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<UserTemplateResponse> templates = userTemplateService.getUserDeployedTemplates(user);
        return ResponseEntity.ok(templates);
    }
    
    @PostMapping("/select/{templateId}")
    public ResponseEntity<UserTemplateResponse> selectTemplate(
            @PathVariable Long templateId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userTemplateService.selectTemplate(templateId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{userTemplateId}")
    public ResponseEntity<UserTemplateResponse> getUserTemplate(
            @PathVariable Long userTemplateId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userTemplateService.getUserTemplateById(userTemplateId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{userTemplateId}")
    public ResponseEntity<UserTemplateResponse> updateUserTemplate(
            @PathVariable Long userTemplateId,
            @RequestBody UserTemplateRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userTemplateService.updateUserTemplate(userTemplateId, request, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{userTemplateId}/deploy")
    public ResponseEntity<UserTemplateResponse> deployTemplate(
            @PathVariable Long userTemplateId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userTemplateService.deployTemplate(userTemplateId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{userTemplateId}/undeploy")
    public ResponseEntity<UserTemplateResponse> undeployTemplate(
            @PathVariable Long userTemplateId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userTemplateService.undeployTemplate(userTemplateId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{userTemplateId}")
    public ResponseEntity<Void> deleteUserTemplate(
            @PathVariable Long userTemplateId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = userTemplateService.deleteUserTemplate(userTemplateId, user);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

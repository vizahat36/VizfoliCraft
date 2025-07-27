package com.yourcompany.portfoliogenerator.controller;

import com.yourcompany.portfoliogenerator.model.GeneratedResume;
import com.yourcompany.portfoliogenerator.model.ResumeTemplate;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.service.ResumeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ResumeController {
    
    private final ResumeGeneratorService resumeGeneratorService;
    
    @GetMapping("/templates")
    public ResponseEntity<List<ResumeTemplate>> getAllTemplates(
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(required = false) ResumeTemplate.TemplateType templateType) {
        
        List<ResumeTemplate> templates;
        if (isPremium != null || templateType != null) {
            templates = resumeGeneratorService.getTemplatesWithFilters(isPremium, templateType);
        } else {
            templates = resumeGeneratorService.getAllActiveTemplates();
        }
        
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/types/{type}")
    public ResponseEntity<List<ResumeTemplate>> getTemplatesByType(
            @PathVariable ResumeTemplate.TemplateType type) {
        
        List<ResumeTemplate> templates = resumeGeneratorService.getTemplatesByType(type);
        return ResponseEntity.ok(templates);
    }
    
    @PostMapping("/generate")
    public ResponseEntity<GeneratedResume> generateResume(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> request) {
        
        Long templateId = Long.valueOf(request.get("templateId").toString());
        GeneratedResume.FileFormat format = GeneratedResume.FileFormat.valueOf(
                request.getOrDefault("format", "PDF").toString().toUpperCase());
        
        GeneratedResume generatedResume = resumeGeneratorService.generateResume(user, templateId, format);
        
        log.info("Resume generation initiated for user: {} with template: {}", user.getUsername(), templateId);
        return ResponseEntity.ok(generatedResume);
    }
    
    @GetMapping("/my-resumes")
    public ResponseEntity<List<GeneratedResume>> getUserResumes(@AuthenticationPrincipal User user) {
        List<GeneratedResume> resumes = resumeGeneratorService.getUserResumes(user);
        return ResponseEntity.ok(resumes);
    }
    
    @GetMapping("/my-resumes/completed")
    public ResponseEntity<List<GeneratedResume>> getUserCompletedResumes(@AuthenticationPrincipal User user) {
        List<GeneratedResume> resumes = resumeGeneratorService.getUserCompletedResumes(user);
        return ResponseEntity.ok(resumes);
    }
    
    @GetMapping("/download/{resumeId}")
    public ResponseEntity<Resource> downloadResume(
            @AuthenticationPrincipal User user,
            @PathVariable Long resumeId) throws IOException {
        
        Resource resource = resumeGeneratorService.downloadResume(user, resumeId);
        
        // Determine content type based on file extension
        String contentType = "application/octet-stream";
        String filename = resource.getFilename();
        if (filename != null) {
            if (filename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filename.endsWith(".html")) {
                contentType = "text/html";
            } else if (filename.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
    
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @AuthenticationPrincipal User user,
            @PathVariable Long resumeId) {
        
        resumeGeneratorService.deleteResume(user, resumeId);
        log.info("Resume deleted by user: {}, resumeId: {}", user.getUsername(), resumeId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/formats")
    public ResponseEntity<GeneratedResume.FileFormat[]> getAvailableFormats() {
        return ResponseEntity.ok(GeneratedResume.FileFormat.values());
    }
    
    @GetMapping("/template-types")
    public ResponseEntity<ResumeTemplate.TemplateType[]> getTemplateTypes() {
        return ResponseEntity.ok(ResumeTemplate.TemplateType.values());
    }
}

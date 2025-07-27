package com.yourcompany.portfoliogenerator.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.yourcompany.portfoliogenerator.model.*;
import com.yourcompany.portfoliogenerator.repository.GeneratedResumeRepository;
import com.yourcompany.portfoliogenerator.repository.ResumeTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeGeneratorService {
    
    private final ResumeTemplateRepository resumeTemplateRepository;
    private final GeneratedResumeRepository generatedResumeRepository;
    private final PortfolioDataGenerationService portfolioDataGenerationService;
    private final TemplateEngine templateEngine;
    
    @Value("${app.resume.storage.path:./resumes}")
    private String resumeStoragePath;
    
    @Value("${app.resume.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public List<ResumeTemplate> getAllActiveTemplates() {
        return resumeTemplateRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<ResumeTemplate> getTemplatesByType(ResumeTemplate.TemplateType templateType) {
        return resumeTemplateRepository.findByTemplateTypeAndIsActiveTrue(templateType);
    }
    
    public List<ResumeTemplate> getTemplatesWithFilters(Boolean isPremium, ResumeTemplate.TemplateType templateType) {
        return resumeTemplateRepository.findTemplatesWithFilters(isPremium, templateType);
    }
    
    public GeneratedResume generateResume(User user, Long templateId, GeneratedResume.FileFormat format) {
        log.info("Starting resume generation for user {} with template {} in format {}", 
                user.getUsername(), templateId, format);
        
        ResumeTemplate template = resumeTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
        
        // Create initial record
        GeneratedResume generatedResume = GeneratedResume.builder()
                .user(user)
                .template(template)
                .format(format)
                .status(GeneratedResume.GenerationStatus.PROCESSING)
                .fileName(generateFileName(user, template, format))
                .build();
        
        generatedResume = generatedResumeRepository.save(generatedResume);
        
        try {
            // Generate portfolio data
            PortfolioData portfolioData = portfolioDataGenerationService.generatePortfolioData(user).block();
            
            // Generate the file based on format
            String filePath = switch (format) {
                case PDF -> generatePdfResume(generatedResume, template, portfolioData);
                case HTML -> generateHtmlResume(generatedResume, template, portfolioData);
                case DOCX -> generateDocxResume(generatedResume, template, portfolioData);
            };
            
            // Update file info
            File file = new File(filePath);
            generatedResume.setFilePath(filePath);
            generatedResume.setFileSize(file.length());
            generatedResume.setStatus(GeneratedResume.GenerationStatus.COMPLETED);
            
        } catch (Exception e) {
            log.error("Error generating resume for user {}: {}", user.getUsername(), e.getMessage(), e);
            generatedResume.setStatus(GeneratedResume.GenerationStatus.FAILED);
        }
        
        return generatedResumeRepository.save(generatedResume);
    }
    
    private String generatePdfResume(GeneratedResume generatedResume, ResumeTemplate template, PortfolioData portfolioData) throws IOException {
        String htmlContent = generateHtmlContent(template, portfolioData);
        
        // Create storage directory
        Path storageDir = Paths.get(resumeStoragePath);
        Files.createDirectories(storageDir);
        
        String fileName = generatedResume.getFileName();
        Path outputPath = storageDir.resolve(fileName);
        
        try (OutputStream os = Files.newOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, baseUrl);
            builder.toStream(os);
            builder.run();
        }
        
        log.info("Generated PDF resume: {}", outputPath.toString());
        return outputPath.toString();
    }
    
    private String generateHtmlResume(GeneratedResume generatedResume, ResumeTemplate template, PortfolioData portfolioData) throws IOException {
        String htmlContent = generateHtmlContent(template, portfolioData);
        
        // Create storage directory
        Path storageDir = Paths.get(resumeStoragePath);
        Files.createDirectories(storageDir);
        
        String fileName = generatedResume.getFileName();
        Path outputPath = storageDir.resolve(fileName);
        
        Files.write(outputPath, htmlContent.getBytes());
        
        log.info("Generated HTML resume: {}", outputPath.toString());
        return outputPath.toString();
    }
    
    private String generateDocxResume(GeneratedResume generatedResume, ResumeTemplate template, PortfolioData portfolioData) throws IOException {
        // For DOCX generation, we would use Apache POI
        // For now, we'll generate HTML and convert later
        return generateHtmlResume(generatedResume, template, portfolioData);
    }
    
    private String generateHtmlContent(ResumeTemplate template, PortfolioData portfolioData) {
        Context context = new Context();
        
        // Add portfolio data to template context
        context.setVariable("personalInfo", portfolioData.getPersonalInfo());
        context.setVariable("projects", portfolioData.getProjects());
        context.setVariable("skills", portfolioData.getSkills());
        context.setVariable("experience", portfolioData.getExperience());
        context.setVariable("education", portfolioData.getEducation());
        context.setVariable("socialLinks", portfolioData.getSocialLinks());
        context.setVariable("stats", portfolioData.getStats());
        context.setVariable("lastUpdated", portfolioData.getLastUpdated());
        
        // Add formatting helpers
        context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("MMM yyyy"));
        context.setVariable("fullDateFormatter", DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        
        // Process the template
        String templateContent = template.getHtmlTemplate();
        if (templateContent == null || templateContent.trim().isEmpty()) {
            templateContent = getDefaultTemplate();
        }
        
        // Use Thymeleaf to process the template
        return templateEngine.process("string:" + templateContent, context);
    }
    
    private String generateFileName(User user, ResumeTemplate template, GeneratedResume.FileFormat format) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = format.name().toLowerCase();
        return String.format("%s_%s_%s_%s.%s", 
                user.getUsername(), 
                template.getName().replaceAll("[^a-zA-Z0-9]", ""), 
                timestamp, 
                UUID.randomUUID().toString().substring(0, 8),
                extension);
    }
    
    public List<GeneratedResume> getUserResumes(User user) {
        return generatedResumeRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<GeneratedResume> getUserCompletedResumes(User user) {
        return generatedResumeRepository.findCompletedResumesByUser(user);
    }
    
    public Resource downloadResume(User user, Long resumeId) throws IOException {
        GeneratedResume resume = generatedResumeRepository.findByIdAndUser(resumeId, user)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        
        if (resume.getStatus() != GeneratedResume.GenerationStatus.COMPLETED) {
            throw new RuntimeException("Resume is not ready for download");
        }
        
        Path filePath = Paths.get(resume.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Resume file not found");
        }
        
        // Increment download count
        resume.setDownloadCount(resume.getDownloadCount() + 1);
        generatedResumeRepository.save(resume);
        
        return new UrlResource(filePath.toUri());
    }
    
    public void deleteResume(User user, Long resumeId) {
        GeneratedResume resume = generatedResumeRepository.findByIdAndUser(resumeId, user)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        
        // Delete file
        try {
            Path filePath = Paths.get(resume.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete resume file: {}", resume.getFilePath());
        }
        
        // Delete record
        generatedResumeRepository.delete(resume);
    }
    
    public void cleanupExpiredResumes() {
        List<GeneratedResume> expiredResumes = generatedResumeRepository.findExpiredResumes(LocalDateTime.now());
        
        for (GeneratedResume resume : expiredResumes) {
            try {
                Path filePath = Paths.get(resume.getFilePath());
                Files.deleteIfExists(filePath);
                generatedResumeRepository.delete(resume);
                log.info("Cleaned up expired resume: {}", resume.getFileName());
            } catch (Exception e) {
                log.error("Error cleaning up resume {}: {}", resume.getFileName(), e.getMessage());
            }
        }
    }
    
    private String getDefaultTemplate() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Resume</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .section { margin-bottom: 25px; }
                        .section-title { font-size: 18px; font-weight: bold; border-bottom: 2px solid #333; padding-bottom: 5px; }
                        .project, .experience, .education { margin-bottom: 15px; }
                        .skills { display: flex; flex-wrap: wrap; gap: 10px; }
                        .skill { background: #f0f0f0; padding: 5px 10px; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1 th:text="${personalInfo.name}">Name</h1>
                        <h3 th:text="${personalInfo.title}">Title</h3>
                        <p th:text="${personalInfo.email}">Email</p>
                    </div>
                    
                    <div class="section" th:if="${personalInfo.bio}">
                        <div class="section-title">Summary</div>
                        <p th:text="${personalInfo.bio}">Bio</p>
                    </div>
                    
                    <div class="section" th:if="${not #lists.isEmpty(skills)}">
                        <div class="section-title">Skills</div>
                        <div class="skills">
                            <span class="skill" th:each="skill : ${skills}" th:text="${skill}">Skill</span>
                        </div>
                    </div>
                    
                    <div class="section" th:if="${not #lists.isEmpty(experience)}">
                        <div class="section-title">Experience</div>
                        <div class="experience" th:each="exp : ${experience}">
                            <h4 th:text="${exp.title}">Title</h4>
                            <p th:text="${exp.company}">Company</p>
                            <p th:text="${exp.description}">Description</p>
                        </div>
                    </div>
                    
                    <div class="section" th:if="${not #lists.isEmpty(projects)}">
                        <div class="section-title">Projects</div>
                        <div class="project" th:each="project : ${projects}">
                            <h4 th:text="${project.name}">Project Name</h4>
                            <p th:text="${project.description}">Description</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}

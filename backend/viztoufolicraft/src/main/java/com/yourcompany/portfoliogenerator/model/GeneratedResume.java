package com.yourcompany.portfoliogenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "generated_resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedResume {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private ResumeTemplate template;
    
    private String fileName;
    
    private String filePath;
    
    private Long fileSize;
    
    private FileFormat format;
    
    private GenerationStatus status;
    
    private Integer downloadCount = 0;
    
    private Boolean isPublic = false;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expiresAt;
    
    // Lifecycle method for MongoDB
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
            // Set expiration to 30 days from creation
            expiresAt = LocalDateTime.now().plusDays(30);
        }
    }
    
    public enum FileFormat {
        PDF,
        HTML,
        DOCX
    }
    
    public enum GenerationStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}

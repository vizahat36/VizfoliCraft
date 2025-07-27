package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.ActivityLog;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {
    
    private final ActivityLogRepository activityLogRepository;
    
    @Async
    public void logActivity(User user, ActivityLog.ActivityType type, String description, String entityType, String entityId) {
        try {
            HttpServletRequest request = getCurrentRequest();
            
            ActivityLog activityLog = ActivityLog.builder()
                    .user(user)
                    .action(type.name())
                    .description(description)
                    .type(type)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(getUserAgent(request))
                    .sessionId(getSessionId(request))
                    .timestamp(LocalDateTime.now())
                    .build();
            
            activityLogRepository.save(activityLog);
            log.debug("Activity logged: {} - {}", type, description);
            
        } catch (Exception e) {
            log.error("Failed to log activity: {} - {}", type, description, e);
        }
    }
    
    @Async
    public void logActivity(User user, ActivityLog.ActivityType type, String description, 
                           String entityType, String entityId, String oldValue, String newValue) {
        try {
            HttpServletRequest request = getCurrentRequest();
            
            ActivityLog activityLog = ActivityLog.builder()
                    .user(user)
                    .action(type.name())
                    .description(description)
                    .type(type)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(getUserAgent(request))
                    .sessionId(getSessionId(request))
                    .timestamp(LocalDateTime.now())
                    .build();
            
            activityLogRepository.save(activityLog);
            log.debug("Activity logged with values: {} - {}", type, description);
            
        } catch (Exception e) {
            log.error("Failed to log activity with values: {} - {}", type, description, e);
        }
    }
    
    @Async
    public void logSystemActivity(ActivityLog.ActivityType type, String description, String entityType, String entityId) {
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .action(type.name())
                    .description(description)
                    .type(type)
                    .entityType(entityType)
                    .entityId(entityId)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            activityLogRepository.save(activityLog);
            log.debug("System activity logged: {} - {}", type, description);
            
        } catch (Exception e) {
            log.error("Failed to log system activity: {} - {}", type, description, e);
        }
    }
    
    public Page<ActivityLog> getUserActivities(User user, Pageable pageable) {
        return activityLogRepository.findByUserOrderByTimestampDesc(user, pageable);
    }
    
    public Page<ActivityLog> getAllActivities(Pageable pageable) {
        return activityLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
    
    public Page<ActivityLog> getActivitiesByType(ActivityLog.ActivityType type, Pageable pageable) {
        return activityLogRepository.findByTypeOrderByTimestampDesc(type, pageable);
    }
    
    public Page<ActivityLog> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return activityLogRepository.findByDateRange(startDate, endDate, pageable);
    }
    
    public List<ActivityLog> getUserActivitiesByType(User user, ActivityLog.ActivityType type) {
        return activityLogRepository.findByUserAndTypeOrderByTimestampDesc(user, type);
    }
    
    public List<ActivityLog> getUserActivitiesByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByUserAndDateRange(userId, startDate, endDate);
    }
    
    public long getActivityCountByType(ActivityLog.ActivityType type) {
        return activityLogRepository.countByType(type);
    }
    
    public long getUserActivityCount(User user) {
        return activityLogRepository.countByUser(user);
    }
    
    public long getActivityCountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.countByDateRange(startDate, endDate);
    }
    
    public void cleanupOldActivities(LocalDateTime beforeDate) {
        log.info("Cleaning up activity logs before: {}", beforeDate);
        activityLogRepository.deleteByTimestampBefore(beforeDate);
        log.info("Activity logs cleanup completed");
    }
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? requestAttributes.getRequest() : null;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getUserAgent(HttpServletRequest request) {
        return request != null ? request.getHeader("User-Agent") : "unknown";
    }
    
    private String getSessionId(HttpServletRequest request) {
        return request != null && request.getSession(false) != null ? 
                request.getSession().getId() : "no-session";
    }
}

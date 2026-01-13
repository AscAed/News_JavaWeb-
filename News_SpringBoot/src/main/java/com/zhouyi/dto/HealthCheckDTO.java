package com.zhouyi.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统健康检查响应DTO
 */
public class HealthCheckDTO {
    
    private String status;
    private LocalDateTime timestamp;
    private Map<String, ComponentHealthDTO> components;

    // Constructors
    public HealthCheckDTO() {}

    public HealthCheckDTO(String status, LocalDateTime timestamp, Map<String, ComponentHealthDTO> components) {
        this.status = status;
        this.timestamp = timestamp;
        this.components = components;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, ComponentHealthDTO> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ComponentHealthDTO> components) {
        this.components = components;
    }

    /**
     * 组件健康状态DTO
     */
    public static class ComponentHealthDTO {
        private String status;
        private Long responseTime;
        private Map<String, Object> details;

        public ComponentHealthDTO() {}

        public ComponentHealthDTO(String status, Long responseTime, Map<String, Object> details) {
            this.status = status;
            this.responseTime = responseTime;
            this.details = details;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(Long responseTime) {
            this.responseTime = responseTime;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }
}

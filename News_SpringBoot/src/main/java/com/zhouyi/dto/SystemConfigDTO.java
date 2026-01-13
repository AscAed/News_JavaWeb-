package com.zhouyi.dto;

import java.util.Map;

/**
 * 系统配置响应DTO
 */
public class SystemConfigDTO {
    
    private Map<String, Object> system;
    private Map<String, Object> upload;
    private Map<String, Object> security;

    // Constructors
    public SystemConfigDTO() {}

    public SystemConfigDTO(Map<String, Object> system, Map<String, Object> upload, Map<String, Object> security) {
        this.system = system;
        this.upload = upload;
        this.security = security;
    }

    // Getters and Setters
    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }

    public Map<String, Object> getUpload() {
        return upload;
    }

    public void setUpload(Map<String, Object> upload) {
        this.upload = upload;
    }

    public Map<String, Object> getSecurity() {
        return security;
    }

    public void setSecurity(Map<String, Object> security) {
        this.security = security;
    }
}

package com.zhouyi.service.impl;

import com.zhouyi.dto.SystemConfigDTO;
import com.zhouyi.entity.SystemConfig;
import com.zhouyi.mapper.SystemConfigMapper;
import com.zhouyi.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 系统配置服务实现
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public SystemConfigDTO getSystemConfig(String category) {
        Map<String, Object> system = new HashMap<>();
        Map<String, Object> upload = new HashMap<>();
        Map<String, Object> security = new HashMap<>();

        List<SystemConfig> configs;
        if (category != null && !category.isEmpty()) {
            configs = systemConfigMapper.selectByConfigType(category);
        } else {
            configs = systemConfigMapper.selectAll();
        }

        for (SystemConfig config : configs) {
            String value = (String) parseConfigValue(config.getConfigValue(), config.getConfigType());
            
            switch (config.getConfigType()) {
                case "system":
                    system.put(config.getConfigKey(), value);
                    break;
                case "upload":
                    upload.put(config.getConfigKey(), value);
                    break;
                case "security":
                    security.put(config.getConfigKey(), value);
                    break;
            }
        }

        return new SystemConfigDTO(system, upload, security);
    }

    @Override
    @Transactional
    public Map<String, Object> updateSystemConfig(Map<String, String> configs) {
        List<String> updatedKeys = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();
            
            SystemConfig existingConfig = systemConfigMapper.selectByConfigKey(configKey);
            if (existingConfig != null) {
                // 更新现有配置
                systemConfigMapper.updateByConfigKey(configKey, configValue, existingConfig.getDescription());
                updatedKeys.add(configKey);
            } else {
                // 创建新配置
                SystemConfig newConfig = new SystemConfig();
                newConfig.setConfigKey(configKey);
                newConfig.setConfigValue(configValue);
                newConfig.setConfigType(determineConfigType(configKey));
                newConfig.setDescription("动态配置");
                newConfig.setIsSystem(false);
                systemConfigMapper.insert(newConfig);
                updatedKeys.add(configKey);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("updated_keys", updatedKeys);
        result.put("updated_time", java.time.LocalDateTime.now().toString());
        
        return result;
    }

    @Override
    public String getConfigValue(String configKey) {
        SystemConfig config = systemConfigMapper.selectByConfigKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional
    public void setConfigValue(String configKey, String configValue, String description) {
        SystemConfig existingConfig = systemConfigMapper.selectByConfigKey(configKey);
        if (existingConfig != null) {
            systemConfigMapper.updateByConfigKey(configKey, configValue, description);
        } else {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigType(determineConfigType(configKey));
            newConfig.setDescription(description);
            newConfig.setIsSystem(false);
            systemConfigMapper.insert(newConfig);
        }
    }

    @Override
    @Transactional
    public void initDefaultConfigs() {
        // 检查是否已有配置
        List<SystemConfig> existingConfigs = systemConfigMapper.selectAll();
        if (!existingConfigs.isEmpty()) {
            return; // 已有配置，不重复初始化
        }

        // 系统配置
        List<SystemConfig> defaultConfigs = Arrays.asList(
            // System configs
            new SystemConfig("site_name", "新闻头条系统", "system", "网站名称", false),
            new SystemConfig("site_description", "基于Spring Boot的新闻管理系统", "system", "网站描述", false),
            new SystemConfig("version", "4.4.0", "system", "系统版本", true),
            new SystemConfig("maintenance_mode", "false", "system", "维护模式", false),
            
            // Upload configs
            new SystemConfig("max_file_size", "5242880", "upload", "最大文件大小(字节)", false),
            new SystemConfig("allowed_types", "[\"jpg\", \"jpeg\", \"png\", \"gif\"]", "upload", "允许的文件类型", false),
            new SystemConfig("image_quality", "85", "upload", "图片质量", false),
            
            // Security configs
            new SystemConfig("jwt_expiration", "86400", "security", "JWT过期时间(秒)", false),
            new SystemConfig("password_min_length", "6", "security", "密码最小长度", false),
            new SystemConfig("max_login_attempts", "5", "security", "最大登录尝试次数", false)
        );

        for (SystemConfig config : defaultConfigs) {
            systemConfigMapper.insert(config);
        }
    }

    /**
     * 解析配置值
     */
    private Object parseConfigValue(String value, String type) {
        if ("JSON".equals(type) || value.startsWith("[") || value.startsWith("{")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readTree(value);
            } catch (Exception e) {
                return value;
            }
        } else if ("NUMBER".equals(type) || value.matches("\\d+")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    return value;
                }
            }
        } else if ("BOOLEAN".equals(type) || "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

    /**
     * 根据配置键确定配置类型
     */
    private String determineConfigType(String configKey) {
        if (configKey.startsWith("site_") || configKey.equals("version") || configKey.equals("maintenance_mode")) {
            return "system";
        } else if (configKey.startsWith("max_file_") || configKey.startsWith("allowed_") || configKey.startsWith("image_")) {
            return "upload";
        } else if (configKey.startsWith("jwt_") || configKey.startsWith("password_") || configKey.startsWith("max_login_")) {
            return "security";
        }
        return "system"; // 默认类型
    }
}

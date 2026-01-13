package com.zhouyi.service;

import com.zhouyi.dto.SystemConfigDTO;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {

    /**
     * 获取系统配置
     * @param category 配置分类：system, upload, security 或 null(全部)
     * @return 系统配置
     */
    SystemConfigDTO getSystemConfig(String category);

    /**
     * 更新系统配置
     * @param configs 配置键值对
     * @return 更新的键列表
     */
    Map<String, Object> updateSystemConfig(Map<String, String> configs);

    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 设置配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @param description 描述
     */
    void setConfigValue(String configKey, String configValue, String description);

    /**
     * 初始化默认配置
     */
    void initDefaultConfigs();
}

package com.zhouyi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RSS订阅源实体 - MySQL存储
 * 用于存储RSS订阅源的元数据信息
 */
@Data
public class RssSubscription {

    /**
     * ID
     */
    private Long id;

    /**
     * 订阅源名称
     */
    private String name;

    /**
     * RSS URL
     */
    private String url;

    /**
     * 描述
     */
    private String description;

    /**
     * 分类 (e.g., technology, politics, sports)
     */
    private String category;

    /**
     * 语言代码 (e.g., zh, en)
     */
    private String language;

    /**
     * 是否激活
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     * 采集间隔(分钟)
     */
    @JsonProperty("fetch_interval")
    private Integer fetchInterval;

    /**
     * 采集状态 (pending, success, failed)
     */
    @JsonProperty("fetch_status")
    private String fetchStatus;

    /**
     * 错误信息
     */
    @JsonProperty("error_message")
    private String errorMessage;

    /**
     * 文章总数
     */
    @JsonProperty("total_articles")
    private Integer totalArticles;

    /**
     * 最后采集时间
     */
    @JsonProperty("last_fetched_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastFetchedAt;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;
}

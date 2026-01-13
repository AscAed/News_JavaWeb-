package com.zhouyi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RSS订阅源实体
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

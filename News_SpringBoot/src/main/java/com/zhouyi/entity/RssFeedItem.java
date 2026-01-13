package com.zhouyi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RSS文章条目实体
 */
@Data
public class RssFeedItem {

    /**
     * ID
     */
    private Long id;

    /**
     * 订阅源ID
     */
    @JsonProperty("subscription_id")
    private Long subscriptionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接
     */
    private String link;

    /**
     * 描述/内容
     */
    private String description;

    /**
     * 发布时间
     */
    @JsonProperty("pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime pubDate;

    /**
     * 唯一标识
     */
    private String guid;

    /**
     * 作者
     */
    private String author;

    /**
     * 创建时间（采集入库时间）
     */
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

}

package com.zhouyi.entity.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * RSS分类/标签统计MongoDB实体
 */
@Data
@Document(collection = "rss_categories")
public class RssCategory {

    @Id
    private String id;

    /**
     * 分类名称
     */
    @Field("name")
    private String name;

    /**
     * 分类描述
     */
    @Field("description")
    private String description;

    /**
     * 文章数量
     */
    @Field("article_count")
    private Integer articleCount = 0;

    /**
     * 最后使用时间
     */
    @Field("last_used_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUsedAt;

    /**
     * 创建时间
     */
    @Field("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    /**
     * 是否启用
     */
    @Field("enabled")
    private Boolean enabled = true;

    /**
     * 颜色标签（用于前端显示）
     */
    @Field("color")
    private String color;

    /**
     * 图标
     */
    @Field("icon")
    private String icon;

}

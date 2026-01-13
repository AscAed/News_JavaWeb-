package com.zhouyi.entity.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RSS文章MongoDB实体
 */
@Data
@Document(collection = "rss_articles")
public class RssArticle {

    @Id
    private String id;

    /**
     * 订阅源ID
     */
    @Field("subscription_id")
    private String subscriptionId;

    /**
     * 订阅源名称
     */
    @Field("subscription_name")
    private String subscriptionName;

    /**
     * 文章标题
     */
    @Field("title")
    private String title;

    /**
     * 文章链接
     */
    @Field("link")
    private String link;

    /**
     * 文章描述/内容（HTML格式）
     */
    @Field("description")
    private String description;

    /**
     * 纯文本内容（去除HTML标签）
     */
    @Field("content_text")
    private String contentText;

    /**
     * 发布时间
     */
    @Field("pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime pubDate;

    /**
     * 唯一标识符
     */
    @Field("guid")
    private String guid;

    /**
     * 作者
     */
    @Field("author")
    private String author;

    /**
     * 标签/分类列表
     */
    @Field("categories")
    private List<String> categories;

    /**
     * 内容摘要（自动生成）
     */
    @Field("summary")
    private String summary;

    /**
     * 字数统计
     */
    @Field("word_count")
    private Integer wordCount;

    /**
     * 采集时间
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
     * 内容类型
     */
    @Field("content_type")
    private String contentType = "rss";

    /**
     * 语言
     */
    @Field("language")
    private String language;

    /**
     * 关键词（自动提取）
     */
    @Field("keywords")
    private List<String> keywords;

    /**
     * 情感分析结果
     */
    @Field("sentiment")
    private SentimentAnalysis sentiment;

    /**
     * 重要度评分
     */
    @Field("importance_score")
    private Double importanceScore;

    /**
     * 是否已读
     */
    @Field("is_read")
    private Boolean isRead = false;

    /**
     * 是否收藏
     */
    @Field("is_favorite")
    private Boolean isFavorite = false;

    /**
     * 情感分析结果内部类
     */
    @Data
    public static class SentimentAnalysis {
        @Field("score")
        private Double score;

        @Field("label")
        private String label; // positive, negative, neutral

        @Field("confidence")
        private Double confidence;

    }

}

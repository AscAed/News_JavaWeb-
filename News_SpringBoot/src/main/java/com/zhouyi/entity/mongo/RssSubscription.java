package com.zhouyi.entity.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RSS订阅源MongoDB实体
 */
@Data
@Document(collection = "rss_subscriptions")
public class RssSubscription {

    @Id
    private String id;

    /**
     * 订阅源名称
     */
    @Field("name")
    private String name;

    /**
     * RSS URL
     */
    @Field("url")
    private String url;

    /**
     * 频道标题
     */
    @Field("channel_title")
    private String channelTitle;

    /**
     * 频道链接
     */
    @Field("channel_link")
    private String channelLink;

    /**
     * 频道描述
     */
    @Field("channel_description")
    private String channelDescription;

    /**
     * 描述
     */
    @Field("description")
    private String description;

    /**
     * 语言
     */
    @Field("language")
    private String language;

    /**
     * 版权信息
     */
    @Field("copyright")
    private String copyright;

    /**
     * 责任编辑
     */
    @Field("managing_editor")
    private String managingEditor;

    /**
     * 发布时间
     */
    @Field("pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime pubDate;

    /**
     * 生成器信息
     */
    @Field("generator")
    private String generator;

    /**
     * 网站管理员
     */
    @Field("web_master")
    private String webMaster;

    /**
     * 最后构建时间
     */
    @Field("last_build_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastBuildDate;

    /**
     * 图标信息
     */
    @Field("image")
    private RssImageInfo image;

    /**
     * 最后采集时间
     */
    @Field("last_fetched_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastFetchedAt;

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
     * 采集状态
     */
    @Field("fetch_status")
    private String fetchStatus;

    /**
     * 错误信息
     */
    @Field("error_message")
    private String errorMessage;

    /**
     * RSS图标信息内部类
     */
    @Data
    public static class RssImageInfo {
        @Field("url")
        private String url;

        @Field("title")
        private String title;

        @Field("link")
        private String link;
    }
}

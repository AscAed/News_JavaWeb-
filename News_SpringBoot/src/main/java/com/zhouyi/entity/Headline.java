package com.zhouyi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻头条实体类（MySQL存储核心信息）
 */
@Data
public class Headline {

    /**
     * 头条新闻ID
     */
    @JsonProperty("id")
    private Integer hid;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻摘要
     */
    private String summary;

    /**
     * 封面图片URL
     */
    @JsonProperty("cover_image")
    private String coverImage;

    /**
     * 标签，多个标签用逗号分隔
     */
    private String tags;

    /**
     * 新闻类型ID
     */
    @JsonProperty("type_id")
    private Integer type;

    /**
     * 新闻类型名称
     */
    @JsonProperty("type_name")
    private String typeName;

    /**
     * 浏览量
     */
    @JsonProperty("page_views")
    private Integer pageViews;

    /**
     * 点赞数
     */
    @JsonProperty("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @JsonProperty("comment_count")
    private Integer commentCount;

    /**
     * 发布者ID
     */
    @JsonProperty("author_id")
    private Integer publisher;

    /**
     * 发布者姓名
     */
    private String author;

    /**
     * 新闻状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;

    /**
     * 是否置顶：0-否，1-是
     */
    @JsonProperty("is_top")
    private Integer isTop;

    /**
     * 创建时间
     */
    @JsonProperty("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @JsonProperty("updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedTime;

    /**
     * 发布时间
     */
    @JsonProperty("published_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime publishedTime;

}

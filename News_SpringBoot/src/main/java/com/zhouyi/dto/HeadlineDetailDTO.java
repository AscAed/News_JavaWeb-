package com.zhouyi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻头条详情DTO - 符合API文档规范
 */
@Data
public class HeadlineDetailDTO {
    
    /**
     * 新闻ID（API统一字段）
     */
    @JsonProperty("id")
    private Integer id;
    
    /**
     * 新闻标题
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * 新闻正文内容（富文本HTML）
     */
    @JsonProperty("content")
    private String content;

    /**
     * 新闻摘要
     */
    @JsonProperty("summary")
    private String summary;
    
    /**
     * 新闻类型ID
     */
    @JsonProperty("type_id")
    private Integer typeId;
    
    /**
     * 新闻类型名称
     */
    @JsonProperty("type_name")
    private String typeName;
    
    /**
     * 作者ID
     */
    @JsonProperty("author_id")
    private Integer authorId;
    
    /**
     * 作者姓名
     */
    @JsonProperty("author")
    private String author;
    
    /**
     * 作者头像URL
     */
    @JsonProperty("author_avatar")
    private String authorAvatar;
    
    /**
     * 封面图片URL
     */
    @JsonProperty("cover_image")
    private String coverImage;
    
    /**
     * 标签列表
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 关键词
     */
    @JsonProperty("keywords")
    private String keywords;
    
    /**
     * 新闻状态：0-草稿，1-已发布，2-已下线
     */
    @JsonProperty("status")
    private Integer status;
    
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
     * 分享数
     */
    @JsonProperty("share_count")
    private Integer shareCount;

    /**
     * 是否置顶：false-否，true-是
     */
    @JsonProperty("is_top")
    private Boolean isTop;

    /**
     * 阅读时间（分钟）
     */
    @JsonProperty("reading_time")
    private Integer readingTime;

    /**
     * 字数统计
     */
    @JsonProperty("word_count")
    private Integer wordCount;

    /**
     * 发布时间
     */
    @JsonProperty("published_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime publishedTime;
    
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
     * SEO标题
     */
    @JsonProperty("seo_title")
    private String seoTitle;

    /**
     * SEO描述
     */
    @JsonProperty("seo_description")
    private String seoDescription;

    /**
     * SEO关键词
     */
    @JsonProperty("seo_keywords")
    private String seoKeywords;

    // Manual setters to workaround Lombok issues
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public void setReadingTime(Integer readingTime) {
        this.readingTime = readingTime;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public void setPublishedTime(LocalDateTime publishedTime) {
        this.publishedTime = publishedTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }

    public void setSeoKeywords(String seoKeywords) {
        this.seoKeywords = seoKeywords;
    }
}

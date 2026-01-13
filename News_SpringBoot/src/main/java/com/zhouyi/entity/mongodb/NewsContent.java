package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻内容实体类（MongoDB存储）
 * 对应news集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "news")
public class NewsContent {

    @Id
    private String id; // MongoDB主键

    @Field("hid")
    private Integer hid; // 关联MySQL中的头条ID

    @Field("news_id")
    private Integer newsId; // 关联MySQL中的新闻ID（兼容字段）

    @Field("title")
    private String title; // 新闻标题

    @Field("content")
    private String content; // 新闻正文内容（富文本）

    @Field("summary")
    private String summary; // 新闻摘要

    @Field("cover_image")
    private String coverImage; // 封面图片URL

    @Field("keywords")
    private String keywords; // 关键词，逗号分隔

    @Field("tag_list")
    private List<String> tagList; // 标签列表

    @Field("author")
    private String author; // 作者姓名

    @Field("content_type")
    private String contentType; // 内容类型：text、html、markdown

    @Field("word_count")
    private Integer wordCount; // 字数统计

    @Field("reading_time")
    private Integer readingTime; // 预计阅读时间（分钟）

    @Field("author_info")
    private AuthorInfo authorInfo; // 作者信息

    @Field("seo_info")
    private SeoInfo seoInfo; // SEO信息

    @Field("created_at")
    private LocalDateTime createdAt; // 创建时间

    @Field("updated_at")
    private LocalDateTime updatedAt; // 更新时间

    @Field("created_time")
    private LocalDateTime createdTime; // 创建时间（兼容字段）

    @Field("updated_time")
    private LocalDateTime updatedTime; // 更新时间（兼容字段）

    @Field("status")
    private Integer status; // 状态：0-草稿，1-发布，2-下线

}

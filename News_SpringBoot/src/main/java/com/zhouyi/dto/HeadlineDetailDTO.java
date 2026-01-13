package com.zhouyi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻头条详情DTO
 */
@Data
public class HeadlineDetailDTO {
    
    /**
     * 新闻ID（API统一字段）
     */
    private Integer id;
    
    /**
     * 头条新闻ID（数据库字段）
     */
    private Integer hid;
    
    /**
     * 新闻标题
     */
    private String title;
    
    /**
     * 新闻正文内容（富文本HTML）
     */
    private String article;
    
    /**
     * 新闻类型ID
     */
    private Integer type;
    
    /**
     * 新闻类型名称
     */
    private String typeName;
    
    /**
     * 浏览量
     */
    private Integer pageViews;
    
    /**
     * 发布后经过的小时数
     */
    private Integer pastHours;
    
    /**
     * 发布者ID
     */
    private Integer publisher;
    
    /**
     * 发布者姓名
     */
    private String author;
    
    /**
     * 新闻摘要
     */
    private String summary;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 标签，多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 新闻状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;
    
    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedTime;
}

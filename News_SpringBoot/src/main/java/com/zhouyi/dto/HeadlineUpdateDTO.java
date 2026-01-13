package com.zhouyi.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 新闻头条更新DTO
 */
@Data
public class HeadlineUpdateDTO {
    
    /**
     * 新闻ID（API统一字段）
     */
    @NotNull(message = "新闻ID不能为空")
    private Integer id;
    
    /**
     * 头条新闻ID（数据库字段）
     */
    private Integer hid;
    
    /**
     * 新闻标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    /**
     * 新闻类型ID
     */
    @NotNull(message = "新闻类型不能为空")
    private Integer type;
    
    /**
     * 新闻内容
     */
    @NotBlank(message = "新闻内容不能为空")
    private String article;
    
    /**
     * 新闻摘要
     */
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 标签，多个标签用逗号分隔
     */
    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;
}

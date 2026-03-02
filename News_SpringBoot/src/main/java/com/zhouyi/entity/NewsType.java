package com.zhouyi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 新闻分类实体类，对应news_types表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsType {

    private Integer id; // 分类ID (tid)

    private String typeName; // 分类名称 (tname)

    private String description; // 分类描述

    private String iconUrl; // 分类图标URL

    private Integer sortOrder; // 排序顺序

    private Integer status; // 状态：0-禁用，1-启用

    private LocalDateTime createdTime; // 创建时间

    private LocalDateTime updatedTime; // 更新时间

    private Integer newsCount; // 新闻数量（用于统计）

    private String color; // 分类颜色（十六进制格式）

    private String sourceType; // 数据源类型: api or rss

    private String sourceId; // 外部数据源ID
}

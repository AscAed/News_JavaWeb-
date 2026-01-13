package com.zhouyi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分类统计数据传输对象
 */
public class CategoryStatisticsDTO {
    
    @NotBlank(message = "分类名称不能为空")
    private String typeName;
    
    @NotNull(message = "分类新闻数不能为空")
    private Integer count;

    // Getters and Setters
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

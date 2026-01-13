package com.zhouyi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 新闻分类更新DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsTypeUpdateDTO {
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50字符")
    private String name;
    
    @Size(max = 200, message = "分类描述不能超过200字符")
    private String description;
    
    @Min(value = 0, message = "排序顺序不能小于0")
    private Integer sortOrder;
    
    private String icon;
    
    private String color;
}

package com.zhouyi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 新闻分类状态更新DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsTypeStatusDTO {
    
    @Min(value = 0, message = "状态值只能为0或1")
    @Max(value = 1, message = "状态值只能为0或1")
    private Integer status;
    
    private String reason;
}

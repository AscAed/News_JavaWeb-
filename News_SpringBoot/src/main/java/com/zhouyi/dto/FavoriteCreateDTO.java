package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * 收藏创建DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCreateDTO {
    
    @NotNull(message = "新闻ID不能为空")
    @Positive(message = "新闻ID必须为正数")
    private Integer headlineId;
    
    @Size(max = 200, message = "收藏备注不能超过200字符")
    private String note;
}

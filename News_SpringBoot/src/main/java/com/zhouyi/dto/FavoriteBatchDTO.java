package com.zhouyi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 收藏批量操作DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBatchDTO {
    
    @NotBlank(message = "操作类型不能为空")
    @Size(max = 10, message = "操作类型不能超过10字符")
    private String action;  // add 或 remove
    
    @NotNull(message = "新闻ID列表不能为空")
    @Size(min = 1, max = 50, message = "新闻ID数量必须在1-50之间")
    private List<Integer> headlineIds;
    
    @Size(max = 200, message = "收藏备注不能超过200字符")
    private String note;
}

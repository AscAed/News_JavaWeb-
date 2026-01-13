package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * 评论状态更新DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentStatusDTO {
    
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 2, message = "状态值不能大于2")
    private Integer status;  // 0-隐藏，1-显示，2-删除
    
    @Size(max = 200, message = "状态变更原因不能超过200字符")
    private String reason;
}

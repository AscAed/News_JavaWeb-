package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * 评论点赞DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeDTO {
    
    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "^(like|unlike)$", message = "操作类型只能是like或unlike")
    private String action;  // like（点赞），unlike（取消点赞）
}

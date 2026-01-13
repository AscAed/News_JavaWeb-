package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * 评论创建DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDTO {
    
    @NotNull(message = "新闻ID不能为空")
    @Positive(message = "新闻ID必须为正数")
    private Integer headlineId;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000字符之间")
    private String content;
    
    private Integer parentId;  // 父评论ID，用于回复
}

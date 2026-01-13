package com.zhouyi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 收藏实体类，对应favorites表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    
    private Integer id;              // 收藏记录ID
    
    private Integer userId;          // 用户ID
    
    private Integer newsId;          // 新闻ID
    
    private String note;             // 收藏备注
    
    private LocalDateTime createdTime; // 创建时间
    
    private LocalDateTime updatedTime; // 更新时间
}

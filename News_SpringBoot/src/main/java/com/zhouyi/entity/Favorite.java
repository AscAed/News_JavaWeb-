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

    // Explicit getters and setters as fallback for Lombok issues
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}

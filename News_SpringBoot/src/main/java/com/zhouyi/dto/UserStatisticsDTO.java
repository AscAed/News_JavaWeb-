package com.zhouyi.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 用户统计数据传输对象
 */
public class UserStatisticsDTO {
    
    @NotNull(message = "总用户数不能为空")
    private Integer totalUsers;
    
    @NotNull(message = "今日用户数不能为空")
    private Integer todayUsers;
    
    @NotNull(message = "活跃用户数不能为空")
    private Integer activeUsers;

    // Getters and Setters
    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getTodayUsers() {
        return todayUsers;
    }

    public void setTodayUsers(Integer todayUsers) {
        this.todayUsers = todayUsers;
    }

    public Integer getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Integer activeUsers) {
        this.activeUsers = activeUsers;
    }
}

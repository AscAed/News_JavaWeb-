package com.zhouyi.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 系统统计数据传输对象
 */
public class SystemStatisticsDTO {
    
    @NotNull(message = "系统总访问量不能为空")
    private Long totalVisits;
    
    @NotNull(message = "今日访问量不能为空")
    private Long todayVisits;
    
    @NotNull(message = "总评论数不能为空")
    private Long totalComments;
    
    @NotNull(message = "今日评论数不能为空")
    private Long todayComments;
    
    @NotNull(message = "总收藏数不能为空")
    private Long totalFavorites;
    
    @NotNull(message = "今日收藏数不能为空")
    private Long todayFavorites;
    
    @NotNull(message = "文件总数不能为空")
    private Long totalFiles;
    
    @NotNull(message = "文件总大小不能为空")
    private Long totalFileSize;
    
    @NotNull(message = "系统运行天数不能为空")
    private Long systemRunningDays;
    
    private String systemVersion;
    private String databaseStatus;
    private String storageStatus;

    // Getters and Setters
    public Long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Long getTodayVisits() {
        return todayVisits;
    }

    public void setTodayVisits(Long todayVisits) {
        this.todayVisits = todayVisits;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getTodayComments() {
        return todayComments;
    }

    public void setTodayComments(Long todayComments) {
        this.todayComments = todayComments;
    }

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Long getTodayFavorites() {
        return todayFavorites;
    }

    public void setTodayFavorites(Long todayFavorites) {
        this.todayFavorites = todayFavorites;
    }

    public Long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public Long getSystemRunningDays() {
        return systemRunningDays;
    }

    public void setSystemRunningDays(Long systemRunningDays) {
        this.systemRunningDays = systemRunningDays;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getDatabaseStatus() {
        return databaseStatus;
    }

    public void setDatabaseStatus(String databaseStatus) {
        this.databaseStatus = databaseStatus;
    }

    public String getStorageStatus() {
        return storageStatus;
    }

    public void setStorageStatus(String storageStatus) {
        this.storageStatus = storageStatus;
    }
}

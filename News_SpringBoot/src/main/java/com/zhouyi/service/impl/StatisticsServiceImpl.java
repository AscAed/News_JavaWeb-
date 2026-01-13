package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CategoryStatisticsDTO;
import com.zhouyi.dto.NewsStatisticsDTO;
import com.zhouyi.dto.UserStatisticsDTO;
import com.zhouyi.dto.SystemStatisticsDTO;
import com.zhouyi.mapper.StatisticsMapper;
import com.zhouyi.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Result<NewsStatisticsDTO> getNewsStatistics() {
        try {
            NewsStatisticsDTO statistics = new NewsStatisticsDTO();
            
            // 获取基础统计数据
            Integer totalNews = statisticsMapper.getTotalNews();
            Integer todayNews = statisticsMapper.getTodayNews();
            Long totalViews = statisticsMapper.getTotalViews();
            List<CategoryStatisticsDTO> topCategories = statisticsMapper.getTopCategories(10);
            
            // 设置统计数据
            statistics.setTotalNews(totalNews != null ? totalNews : 0);
            statistics.setTodayNews(todayNews != null ? todayNews : 0);
            statistics.setTotalViews(totalViews != null ? totalViews : 0L);
            statistics.setTopCategories(topCategories);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(500, "获取新闻统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<UserStatisticsDTO> getUserStatistics() {
        try {
            UserStatisticsDTO statistics = new UserStatisticsDTO();
            
            // 获取用户统计数据
            Integer totalUsers = statisticsMapper.getTotalUsers();
            Integer todayUsers = statisticsMapper.getTodayUsers();
            Integer activeUsers = statisticsMapper.getActiveUsers();
            
            // 设置统计数据
            statistics.setTotalUsers(totalUsers != null ? totalUsers : 0);
            statistics.setTodayUsers(todayUsers != null ? todayUsers : 0);
            statistics.setActiveUsers(activeUsers != null ? activeUsers : 0);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(500, "获取用户统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<SystemStatisticsDTO> getSystemStatistics() {
        try {
            SystemStatisticsDTO statistics = new SystemStatisticsDTO();
            
            // 获取系统访问统计
            Long totalVisits = statisticsMapper.getTotalVisits();
            Long todayVisits = statisticsMapper.getTodayVisits();
            
            // 获取评论统计
            Long totalComments = statisticsMapper.getTotalComments();
            Long todayComments = statisticsMapper.getTodayComments();
            
            // 获取收藏统计
            Long totalFavorites = statisticsMapper.getTotalFavorites();
            Long todayFavorites = statisticsMapper.getTodayFavorites();
            
            // 获取文件统计
            Long totalFiles = statisticsMapper.getTotalFiles();
            Long totalFileSize = statisticsMapper.getTotalFileSize();
            
            // 计算系统运行天数（假设系统启动时间为2025-01-01）
            LocalDateTime systemStartTime = LocalDateTime.of(2025, 1, 1, 0, 0);
            long systemRunningDays = java.time.Duration.between(systemStartTime, LocalDateTime.now()).toDays();
            
            // 设置统计数据
            statistics.setTotalVisits(totalVisits != null ? totalVisits : 0L);
            statistics.setTodayVisits(todayVisits != null ? todayVisits : 0L);
            statistics.setTotalComments(totalComments != null ? totalComments : 0L);
            statistics.setTodayComments(todayComments != null ? todayComments : 0L);
            statistics.setTotalFavorites(totalFavorites != null ? totalFavorites : 0L);
            statistics.setTodayFavorites(todayFavorites != null ? todayFavorites : 0L);
            statistics.setTotalFiles(totalFiles != null ? totalFiles : 0L);
            statistics.setTotalFileSize(totalFileSize != null ? totalFileSize : 0L);
            statistics.setSystemRunningDays(systemRunningDays);
            statistics.setSystemVersion("2.2.0");
            statistics.setDatabaseStatus("正常");
            statistics.setStorageStatus("正常");
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(500, "获取系统统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> getOverviewStatistics() {
        try {
            // 获取所有统计数据并合并
            Result<NewsStatisticsDTO> newsStats = getNewsStatistics();
            Result<UserStatisticsDTO> userStats = getUserStatistics();
            Result<SystemStatisticsDTO> systemStats = getSystemStatistics();
            
            // 构建概览数据
            java.util.Map<String, Object> overview = new java.util.HashMap<>();
            overview.put("newsStatistics", newsStats.getData());
            overview.put("userStatistics", userStats.getData());
            overview.put("systemStatistics", systemStats.getData());
            
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error(500, "获取系统统计概览失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> getCommentStatistics() {
        try {
            java.util.Map<String, Object> commentStats = new java.util.HashMap<>();
            
            Long totalComments = statisticsMapper.getTotalComments();
            Long todayComments = statisticsMapper.getTodayComments();
            
            commentStats.put("totalComments", totalComments != null ? totalComments : 0L);
            commentStats.put("todayComments", todayComments != null ? todayComments : 0L);
            
            return Result.success(commentStats);
        } catch (Exception e) {
            return Result.error(500, "获取评论统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> getFavoriteStatistics() {
        try {
            java.util.Map<String, Object> favoriteStats = new java.util.HashMap<>();
            
            Long totalFavorites = statisticsMapper.getTotalFavorites();
            Long todayFavorites = statisticsMapper.getTodayFavorites();
            
            favoriteStats.put("totalFavorites", totalFavorites != null ? totalFavorites : 0L);
            favoriteStats.put("todayFavorites", todayFavorites != null ? todayFavorites : 0L);
            
            return Result.success(favoriteStats);
        } catch (Exception e) {
            return Result.error(500, "获取收藏统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> getFileStatistics() {
        try {
            java.util.Map<String, Object> fileStats = new java.util.HashMap<>();
            
            Long totalFiles = statisticsMapper.getTotalFiles();
            Long totalFileSize = statisticsMapper.getTotalFileSize();
            
            fileStats.put("totalFiles", totalFiles != null ? totalFiles : 0L);
            fileStats.put("totalFileSize", totalFileSize != null ? totalFileSize : 0L);
            
            return Result.success(fileStats);
        } catch (Exception e) {
            return Result.error(500, "获取文件统计数据失败：" + e.getMessage());
        }
    }
}

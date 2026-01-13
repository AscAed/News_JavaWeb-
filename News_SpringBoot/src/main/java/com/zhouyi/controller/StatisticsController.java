package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.NewsStatisticsDTO;
import com.zhouyi.dto.UserStatisticsDTO;
import com.zhouyi.dto.SystemStatisticsDTO;
import com.zhouyi.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计分析控制器
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取新闻统计数据（管理员功能）
     * @return 新闻统计数据
     */
    @GetMapping("/news")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<NewsStatisticsDTO> getNewsStatistics() {
        return statisticsService.getNewsStatistics();
    }

    /**
     * 获取用户统计数据（管理员功能）
     * @return 用户统计数据
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserStatisticsDTO> getUserStatistics() {
        return statisticsService.getUserStatistics();
    }

    /**
     * 获取系统统计数据（管理员功能）
     * @return 系统统计数据
     */
    @GetMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SystemStatisticsDTO> getSystemStatistics() {
        return statisticsService.getSystemStatistics();
    }

    /**
     * 获取系统统计概览（管理员功能）
     * @return 完整的系统统计数据
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getOverviewStatistics() {
        return statisticsService.getOverviewStatistics();
    }

    /**
     * 获取评论统计数据（管理员功能）
     * @return 评论统计数据
     */
    @GetMapping("/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getCommentStatistics() {
        return statisticsService.getCommentStatistics();
    }

    /**
     * 获取收藏统计数据（管理员功能）
     * @return 收藏统计数据
     */
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getFavoriteStatistics() {
        return statisticsService.getFavoriteStatistics();
    }

    /**
     * 获取文件统计数据（管理员功能）
     * @return 文件统计数据
     */
    @GetMapping("/files")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getFileStatistics() {
        return statisticsService.getFileStatistics();
    }
}

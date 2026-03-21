package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.NewsStatisticsDTO;
import com.zhouyi.dto.UserStatisticsDTO;
import com.zhouyi.dto.SystemStatisticsDTO;
import com.zhouyi.dto.KeywordStatDTO;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取新闻统计数据（管理员功能）
     * @return 新闻统计数据
     */
    Result<NewsStatisticsDTO> getNewsStatistics();

    /**
     * 获取用户统计数据（管理员功能）
     * @return 用户统计数据
     */
    Result<UserStatisticsDTO> getUserStatistics();

    /**
     * 获取系统统计数据（管理员功能）
     * @return 系统统计数据
     */
    Result<SystemStatisticsDTO> getSystemStatistics();

    /**
     * 获取系统统计概览（管理员功能）
     * @return 完整的系统统计数据
     */
    Result<?> getOverviewStatistics();

    /**
     * 获取评论统计数据（管理员功能）
     * @return 评论统计数据
     */
    Result<?> getCommentStatistics();

    /**
     * 获取收藏统计数据（管理员功能）
     * @return 收藏统计数据
     */
    Result<?> getFavoriteStatistics();

    /**
     * 获取文件统计数据（管理员功能）
     * @return 文件统计数据
     */
    Result<?> getFileStatistics();

    /**
     * 获取热点关键词统计（从 Elasticsearch 聚合）
     * @param size 返回的关键词数量
     * @return 关键词统计列表
     */
    Result<List<KeywordStatDTO>> getTrendingKeywords(Integer size);
}

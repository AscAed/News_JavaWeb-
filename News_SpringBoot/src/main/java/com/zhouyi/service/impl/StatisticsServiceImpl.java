package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CategoryStatisticsDTO;
import com.zhouyi.dto.NewsStatisticsDTO;
import com.zhouyi.dto.UserStatisticsDTO;
import com.zhouyi.dto.SystemStatisticsDTO;
import com.zhouyi.dto.KeywordStatDTO;
import com.zhouyi.mapper.StatisticsMapper;
import com.zhouyi.service.StatisticsService;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.domain.PageRequest;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public Result<NewsStatisticsDTO> getNewsStatistics() {
        try {
            NewsStatisticsDTO statistics = new NewsStatisticsDTO();
            Integer totalNews = statisticsMapper.getTotalNews();
            Integer todayNews = statisticsMapper.getTodayNews();
            Long totalViews = statisticsMapper.getTotalViews();
            List<CategoryStatisticsDTO> topCategories = statisticsMapper.getTopCategories(10);
            
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
            Integer totalUsers = statisticsMapper.getTotalUsers();
            Integer todayUsers = statisticsMapper.getTodayUsers();
            Integer activeUsers = statisticsMapper.getActiveUsers();
            
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
            Long totalVisits = statisticsMapper.getTotalVisits();
            Long todayVisits = statisticsMapper.getTodayVisits();
            Long totalComments = statisticsMapper.getTotalComments();
            Long todayComments = statisticsMapper.getTodayComments();
            Long totalFavorites = statisticsMapper.getTotalFavorites();
            Long todayFavorites = statisticsMapper.getTodayFavorites();
            Long totalFiles = statisticsMapper.getTotalFiles();
            Long totalFileSize = statisticsMapper.getTotalFileSize();
            
            LocalDateTime systemStartTime = LocalDateTime.of(2025, 1, 1, 0, 0);
            long systemRunningDays = java.time.Duration.between(systemStartTime, LocalDateTime.now()).toDays();
            
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
            Result<NewsStatisticsDTO> newsStats = getNewsStatistics();
            Result<UserStatisticsDTO> userStats = getUserStatistics();
            Result<SystemStatisticsDTO> systemStats = getSystemStatistics();
            
            Map<String, Object> overview = new HashMap<>();
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
            Map<String, Object> commentStats = new HashMap<>();
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
            Map<String, Object> favoriteStats = new HashMap<>();
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
            Map<String, Object> fileStats = new HashMap<>();
            Long totalFiles = statisticsMapper.getTotalFiles();
            Long totalFileSize = statisticsMapper.getTotalFileSize();
            fileStats.put("totalFiles", totalFiles != null ? totalFiles : 0L);
            fileStats.put("totalFileSize", totalFileSize != null ? totalFileSize : 0L);
            return Result.success(fileStats);
        } catch (Exception e) {
            return Result.error(500, "获取文件统计数据失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<KeywordStatDTO>> getTrendingKeywords(Integer size) {
        try {
            if (size == null || size <= 0) size = 20;
            final int finalSize = size;

            // 构建聚合查询
            NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .withAggregation("hot_topics", Aggregation.of(a -> a
                    .terms(t -> t.field("title").size(finalSize))))
                .withPageable(PageRequest.of(0, 1)) 
                .build();

            SearchHits<HeadlineEsEntity> searchHits = elasticsearchOperations.search(query, HeadlineEsEntity.class);

            List<KeywordStatDTO> keywords = new ArrayList<>();
            
            // 提取聚合结果 (Spring Data ES 5.x)
            AggregationsContainer<?> aggregations = searchHits.getAggregations();
            if (aggregations != null) {
                ElasticsearchAggregations elcAggs = (ElasticsearchAggregations) aggregations;
                ElasticsearchAggregation agg = elcAggs.get("hot_topics");
                if (agg != null) {
                    Aggregate aggregate = agg.aggregation().getAggregate();
                    if (aggregate.isSterms()) {
                        aggregate.sterms().buckets().array().forEach(bucket -> {
                            keywords.add(new KeywordStatDTO(bucket.key().stringValue(), bucket.docCount()));
                        });
                    }
                }
            }

            return Result.success(keywords);
        } catch (Exception e) {
            return Result.error(500, "关键词聚合失败：" + e.getMessage());
        }
    }
}

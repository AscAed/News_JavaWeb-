package com.zhouyi.mapper;

import com.zhouyi.dto.CategoryStatisticsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计数据访问接口
 */
@Mapper
public interface StatisticsMapper {

        /**
         * 获取总新闻数
         * 
         * @return 总新闻数
         */
        @Select("SELECT COUNT(*) FROM headlines WHERE status = 1")
        Integer getTotalNews();

        /**
         * 获取今日新闻数
         * 
         * @return 今日新闻数
         */
        @Select("SELECT COUNT(*) FROM headlines WHERE CAST(created_time AS DATE) = CURRENT_DATE AND status = 1")
        Integer getTodayNews();

        /**
         * 获取总浏览量
         * 
         * @return 总浏览量
         */
        @Select("SELECT COALESCE(SUM(page_views), 0) FROM headlines WHERE status = 1")
        Long getTotalViews();

        /**
         * 获取分类统计前N名
         * 
         * @param limit 限制数量
         * @return 分类统计列表
         */
        @Select("SELECT nt.tname as typeName, COUNT(h.hid) as count " +
                        "FROM headlines h " +
                        "INNER JOIN news_types nt ON h.type = nt.tid " +
                        "WHERE h.status = 1 AND nt.status = 1 " +
                        "GROUP BY nt.tid, nt.tname " +
                        "ORDER BY count DESC " +
                        "LIMIT #{limit}")
        List<CategoryStatisticsDTO> getTopCategories(int limit);

        /**
         * 获取总用户数
         * 
         * @return 总用户数
         */
        @Select("SELECT COUNT(*) FROM users WHERE status = 1")
        Integer getTotalUsers();

        /**
         * 获取今日注册用户数
         * 
         * @return 今日用户数
         */
        @Select("SELECT COUNT(*) FROM users WHERE CAST(created_time AS DATE) = CURRENT_DATE AND status = 1")
        Integer getTodayUsers();

        /**
         * 获取活跃用户数（最近7天有登录记录）
         * 
         * @return 活跃用户数
         */
        @Select("SELECT COUNT(DISTINCT u.id) FROM users u " +
                        "WHERE u.status = 1 AND u.last_login_time >= TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP)")
        Integer getActiveUsers();

        // ==================== 系统统计相关查询 ====================

        /**
         * 获取系统总访问量（模拟数据，基于浏览量）
         * 
         * @return 总访问量
         */
        @Select("SELECT COALESCE(SUM(page_views), 0) FROM headlines WHERE status = 1")
        Long getTotalVisits();

        /**
         * 获取今日访问量（模拟数据，基于今日浏览量）
         * 
         * @return 今日访问量
         */
        @Select("SELECT COALESCE(SUM(page_views), 0) FROM headlines WHERE CAST(created_time AS DATE) = CURRENT_DATE AND status = 1")
        Long getTodayVisits();

        /**
         * 获取总评论数（从MongoDB获取，这里返回模拟数据）
         * 
         * @return 总评论数
         */
        @Select("SELECT 0") // 实际应该从MongoDB查询，这里返回0作为占位符
        Long getTotalComments();

        /**
         * 获取今日评论数（从MongoDB获取，这里返回模拟数据）
         * 
         * @return 今日评论数
         */
        @Select("SELECT 0") // 实际应该从MongoDB查询，这里返回0作为占位符
        Long getTodayComments();

        /**
         * 获取总收藏数
         * 
         * @return 总收藏数
         */
        @Select("SELECT COUNT(*) FROM favorites")
        Long getTotalFavorites();

        /**
         * 获取今日收藏数
         * 
         * @return 今日收藏数
         */
        @Select("SELECT COUNT(*) FROM favorites WHERE CAST(favorite_time AS DATE) = CURRENT_DATE")
        Long getTodayFavorites();

        /**
         * 获取文件总数（从MongoDB获取，这里返回模拟数据）
         * 
         * @return 文件总数
         */
        @Select("SELECT 0") // 实际应该从MongoDB查询，这里返回0作为占位符
        Long getTotalFiles();

        /**
         * 获取文件总大小（从MinIO获取，这里返回模拟数据）
         * 
         * @return 文件总大小（字节）
         */
        @Select("SELECT 0") // 实际应该从MinIO查询，这里返回0作为占位符
        Long getTotalFileSize();
}

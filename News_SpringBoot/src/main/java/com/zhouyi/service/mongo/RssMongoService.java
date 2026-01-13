package com.zhouyi.service.mongo;

import com.zhouyi.entity.mongo.RssArticle;
import com.zhouyi.entity.mongo.RssCategory;
import com.zhouyi.entity.mongo.RssSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * RSS MongoDB服务接口
 */
public interface RssMongoService {

    /**
     * 采集并保存RSS内容到MongoDB
     *
     * @param subscriptionId 订阅源ID
     * @return 采集结果
     */
    Map<String, Object> fetchAndSaveToMongo(String subscriptionId);

    /**
     * 创建或更新RSS订阅源
     *
     * @param subscription 订阅源信息
     * @return 保存的订阅源
     */
    RssSubscription saveOrUpdateSubscription(RssSubscription subscription);

    /**
     * 根据URL查找订阅源
     *
     * @param url RSS URL
     * @return 订阅源
     */
    RssSubscription findSubscriptionByUrl(String url);

    /**
     * 获取所有启用的订阅源
     *
     * @return 订阅源列表
     */
    List<RssSubscription> getAllEnabledSubscriptions();

    /**
     * 根据分类获取文章
     *
     * @param category 分类名称
     * @return 文章列表
     */
    List<RssArticle> getArticlesByCategory(String category);

    /**
     * 根据订阅源获取文章
     *
     * @param subscriptionId 订阅源ID
     * @return 文章列表
     */
    List<RssArticle> getArticlesBySubscription(String subscriptionId);

    /**
     * 全文搜索文章
     *
     * @param keyword 关键词
     * @return 文章列表
     */
    List<RssArticle> searchArticles(String keyword);

    /**
     * 获取热门分类
     *
     * @return 分类列表
     */
    List<RssCategory> getHotCategories(int limit);

    /**
     * 更新分类统计信息
     *
     * @param categories 分类列表
     */
    void updateCategoryStatistics(List<String> categories);

    /**
     * 清理过期文章
     *
     * @param beforeDate 清理日期
     * @return 清理数量
     */
    long cleanupOldArticles(LocalDateTime beforeDate);

    /**
     * 获取文章统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getArticleStatistics();
}

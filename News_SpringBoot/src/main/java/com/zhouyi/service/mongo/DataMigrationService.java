package com.zhouyi.service.mongo;

import com.zhouyi.entity.RssFeedItem;
import com.zhouyi.entity.mongo.RssArticle;
import com.zhouyi.entity.mongo.RssCategory;
import com.zhouyi.entity.mongo.RssSubscription;
import com.zhouyi.mapper.RssFeedItemMapper;
import com.zhouyi.mapper.RssSubscriptionMapper;
import com.zhouyi.repository.mongo.RssArticleRepository;
import com.zhouyi.repository.mongo.RssCategoryRepository;
import com.zhouyi.repository.mongo.RssSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据迁移服务：将MySQL中的RSS数据迁移到MongoDB
 */
@Service
public class DataMigrationService {

    private static final Logger log = LoggerFactory.getLogger(DataMigrationService.class);

    @Autowired
    private RssSubscriptionMapper mysqlSubscriptionMapper;

    @Autowired
    private RssFeedItemMapper mysqlFeedItemMapper;

    @Autowired
    private RssSubscriptionRepository mongoSubscriptionRepository;

    @Autowired
    private RssArticleRepository mongoArticleRepository;

    @Autowired
    private RssCategoryRepository mongoCategoryRepository;

    /**
     * 迁移所有RSS数据
     */
    @Transactional
    public Map<String, Object> migrateAllData() {
        log.info("开始迁移RSS数据从MySQL到MongoDB");

        Map<String, Object> result = new java.util.HashMap<>();

        try {
            // 1. 迁移订阅源
            int migratedSubscriptions = migrateSubscriptions();

            // 2. 迁移文章
            int migratedArticles = migrateArticles();

            // 3. 更新分类统计
            updateCategoryStatistics();

            result.put("migrated_subscriptions", migratedSubscriptions);
            result.put("migrated_articles", migratedArticles);
            result.put("status", "success");
            result.put("message", "数据迁移完成");

            log.info("RSS数据迁移完成: 订阅源 {}, 文章 {}", migratedSubscriptions, migratedArticles);

        } catch (Exception e) {
            log.error("RSS数据迁移失败", e);
            result.put("status", "error");
            result.put("message", "数据迁移失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 迁移订阅源数据
     */
    private int migrateSubscriptions() {
        log.info("开始迁移RSS订阅源");

        List<com.zhouyi.entity.RssSubscription> mysqlSubscriptions = mysqlSubscriptionMapper.findAll();
        int migratedCount = 0;

        for (com.zhouyi.entity.RssSubscription mysqlSub : mysqlSubscriptions) {
            try {
                // 检查是否已存在
                if (mongoSubscriptionRepository.findByUrl(mysqlSub.getUrl()).isPresent()) {
                    log.debug("订阅源已存在，跳过: {}", mysqlSub.getUrl());
                    continue;
                }

                // 转换为MongoDB实体
                com.zhouyi.entity.mongo.RssSubscription mongoSub = convertToMongoSubscription(mysqlSub);

                // 保存到MongoDB
                mongoSubscriptionRepository.save(mongoSub);
                migratedCount++;

                log.debug("迁移订阅源: {}", mysqlSub.getName());

            } catch (Exception e) {
                log.error("迁移订阅源失败: {} - {}", mysqlSub.getName(), e.getMessage());
            }
        }

        log.info("RSS订阅源迁移完成: {}/{}", migratedCount, mysqlSubscriptions.size());
        return migratedCount;
    }

    /**
     * 迁移文章数据
     */
    private int migrateArticles() {
        log.info("开始迁移RSS文章");

        List<RssFeedItem> mysqlArticles = mysqlFeedItemMapper.findAll();
        int migratedCount = 0;

        for (RssFeedItem mysqlArticle : mysqlArticles) {
            try {
                // 检查是否已存在
                if (mongoArticleRepository.findByLink(mysqlArticle.getLink()).isPresent()) {
                    log.debug("文章已存在，跳过: {}", mysqlArticle.getLink());
                    continue;
                }

                // 获取对应的MongoDB订阅源
                String subscriptionId = findMongoSubscriptionId(mysqlArticle.getSubscriptionId());
                if (subscriptionId == null) {
                    log.warn("找不到对应的MongoDB订阅源，跳过文章: {}", mysqlArticle.getTitle());
                    continue;
                }

                // 转换为MongoDB实体
                RssArticle mongoArticle = convertToMongoArticle(mysqlArticle, subscriptionId);

                // 保存到MongoDB
                mongoArticleRepository.save(mongoArticle);
                migratedCount++;

                log.debug("迁移文章: {}", mysqlArticle.getTitle());

            } catch (Exception e) {
                log.error("迁移文章失败: {} - {}", mysqlArticle.getTitle(), e.getMessage());
            }
        }

        log.info("RSS文章迁移完成: {}/{}", migratedCount, mysqlArticles.size());
        return migratedCount;
    }

    /**
     * 更新分类统计
     */
    private void updateCategoryStatistics() {
        log.info("开始更新分类统计");

        // 从所有文章中提取分类
        List<RssArticle> allArticles = mongoArticleRepository.findAll();
        List<String> allCategories = new ArrayList<>();

        for (RssArticle article : allArticles) {
            if (article.getCategories() != null) {
                allCategories.addAll(article.getCategories());
            }
        }

        // 统计分类
        Map<String, Long> categoryCount = allCategories.stream()
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()));

        // 更新或创建分类记录
        for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
            try {
                String categoryName = entry.getKey();
                Long count = entry.getValue();

                RssCategory category = mongoCategoryRepository.findByName(categoryName)
                        .orElse(new RssCategory());

                category.setName(categoryName);
                category.setArticleCount(count.intValue());
                category.setLastUsedAt(LocalDateTime.now());
                category.setEnabled(true);

                if (category.getCreatedAt() == null) {
                    category.setCreatedAt(LocalDateTime.now());
                }
                category.setUpdatedAt(LocalDateTime.now());

                mongoCategoryRepository.save(category);

            } catch (Exception e) {
                log.error("更新分类统计失败: {} - {}", entry.getKey(), e.getMessage());
            }
        }

        log.info("分类统计更新完成，共处理 {} 个分类", categoryCount.size());
    }

    /**
     * 转换MySQL订阅源为MongoDB订阅源
     */
    private com.zhouyi.entity.mongo.RssSubscription convertToMongoSubscription(
            com.zhouyi.entity.RssSubscription mysqlSub) {
        com.zhouyi.entity.mongo.RssSubscription mongoSub = new com.zhouyi.entity.mongo.RssSubscription();

        mongoSub.setId(String.valueOf(mysqlSub.getId()));
        mongoSub.setName(mysqlSub.getName());
        mongoSub.setUrl(mysqlSub.getUrl());
        mongoSub.setDescription(mysqlSub.getDescription());
        mongoSub.setLastFetchedAt(mysqlSub.getLastFetchedAt());
        mongoSub.setCreatedAt(mysqlSub.getCreatedAt());
        mongoSub.setUpdatedAt(mysqlSub.getUpdatedAt());
        mongoSub.setEnabled(true);
        mongoSub.setFetchStatus("migrated");

        return mongoSub;
    }

    /**
     * 转换MySQL文章为MongoDB文章
     */
    private RssArticle convertToMongoArticle(RssFeedItem mysqlArticle, String subscriptionId) {
        RssArticle mongoArticle = new RssArticle();

        mongoArticle.setId(String.valueOf(mysqlArticle.getId()));
        mongoArticle.setSubscriptionId(subscriptionId);
        mongoArticle.setTitle(mysqlArticle.getTitle());
        mongoArticle.setLink(mysqlArticle.getLink());
        mongoArticle.setDescription(mysqlArticle.getDescription());
        mongoArticle.setGuid(mysqlArticle.getGuid());
        mongoArticle.setAuthor(mysqlArticle.getAuthor());
        mongoArticle.setPubDate(mysqlArticle.getPubDate());
        mongoArticle.setCreatedAt(mysqlArticle.getCreatedAt());
        mongoArticle.setUpdatedAt(LocalDateTime.now());
        mongoArticle.setContentType("rss");
        mongoArticle.setIsRead(false);
        mongoArticle.setIsFavorite(false);

        // 提取纯文本内容
        if (mysqlArticle.getDescription() != null) {
            String contentText = org.jsoup.Jsoup.parse(mysqlArticle.getDescription()).text();
            mongoArticle.setContentText(contentText);
            mongoArticle.setWordCount(contentText.length());

            // 生成摘要
            String summary = contentText.length() > 200 ? contentText.substring(0, 200) + "..." : contentText;
            mongoArticle.setSummary(summary);
        }

        // 计算重要度评分
        double importanceScore = calculateImportanceScore(mongoArticle);
        mongoArticle.setImportanceScore(importanceScore);

        return mongoArticle;
    }

    /**
     * 查找MongoDB订阅源ID
     */
    private String findMongoSubscriptionId(Long mysqlSubscriptionId) {
        // 根据MySQL ID查找对应的MongoDB订阅源
        List<com.zhouyi.entity.mongo.RssSubscription> mongoSubs = mongoSubscriptionRepository.findAll();

        for (com.zhouyi.entity.mongo.RssSubscription mongoSub : mongoSubs) {
            try {
                if (mongoSub.getId().equals(String.valueOf(mysqlSubscriptionId))) {
                    return mongoSub.getId();
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return null;
    }

    /**
     * 计算文章重要度评分
     */
    private double calculateImportanceScore(RssArticle article) {
        double score = 0.0;

        // 基于字数的评分
        if (article.getWordCount() != null) {
            score += Math.min(article.getWordCount() / 1000.0, 2.0);
        }

        // 基于分类数量的评分
        if (article.getCategories() != null) {
            score += article.getCategories().size() * 0.1;
        }

        // 基于标题长度的评分
        if (article.getTitle() != null) {
            score += Math.min(article.getTitle().length() / 100.0, 1.0);
        }

        return Math.round(score * 100.0) / 100.0;
    }
}

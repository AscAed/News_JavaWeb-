package com.zhouyi.test;

import com.zhouyi.service.mongo.DataMigrationService;
import com.zhouyi.service.mongo.RssMongoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * MongoDB RSS功能测试
 */
@Component
@ConditionalOnProperty(name = "app.test.mongo", havingValue = "true")
public class MongoRssTest implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoRssTest.class);

    @Autowired
    private DataMigrationService dataMigrationService;

    @Autowired
    private RssMongoService rssMongoService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始测试MongoDB RSS功能");

        try {
            // 1. 测试数据迁移
            testMigration();

            // 2. 测试RSS采集
            testRssFetch();

            // 3. 测试查询功能
            testQueryFunctions();

            log.info("MongoDB RSS功能测试完成");

        } catch (Exception e) {
            log.error("MongoDB RSS功能测试失败", e);
        }
    }

    private void testMigration() {
        log.info("=== 测试数据迁移 ===");

        try {
            var result = dataMigrationService.migrateAllData();
            log.info("数据迁移结果: {}", result);

        } catch (Exception e) {
            log.error("数据迁移测试失败", e);
        }
    }

    private void testRssFetch() {
        log.info("=== 测试RSS采集 ===");

        try {
            // 获取所有订阅源
            var subscriptions = rssMongoService.getAllEnabledSubscriptions();
            log.info("找到 {} 个订阅源", subscriptions.size());

            if (!subscriptions.isEmpty()) {
                // 测试第一个订阅源的采集
                String subscriptionId = subscriptions.get(0).getId();
                var fetchResult = rssMongoService.fetchAndSaveToMongo(subscriptionId);
                log.info("RSS采集结果: {}", fetchResult);
            }

        } catch (Exception e) {
            log.error("RSS采集测试失败", e);
        }
    }

    private void testQueryFunctions() {
        log.info("=== 测试查询功能 ===");

        try {
            // 测试统计信息
            var stats = rssMongoService.getArticleStatistics();
            log.info("统计信息: {}", stats);

            // 测试热门分类
            var hotCategories = rssMongoService.getHotCategories(5);
            log.info("热门分类: {}", hotCategories);

            // 测试搜索功能
            var searchResults = rssMongoService.searchArticles("中国");
            log.info("搜索结果数量: {}", searchResults.size());

        } catch (Exception e) {
            log.error("查询功能测试失败", e);
        }
    }
}

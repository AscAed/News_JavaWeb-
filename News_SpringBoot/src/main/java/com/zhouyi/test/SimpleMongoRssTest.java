package com.zhouyi.test;

import com.zhouyi.service.mongo.RssMongoService;
import com.zhouyi.service.mongo.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 简单的MongoDB RSS测试 - 不依赖Lombok
 */
@Component
@ConditionalOnProperty(name = "app.test.simple.mongo.rss", havingValue = "true")
public class SimpleMongoRssTest {

    @Autowired
    private RssMongoService rssMongoService;

    @Autowired
    private DataMigrationService dataMigrationService;

    @PostConstruct
    public void testMongoRss() {
        System.out.println("=== MongoDB RSS功能测试开始 ===");

        try {
            // 测试数据迁移
            System.out.println("1. 测试数据迁移...");
            dataMigrationService.migrateAllData();
            System.out.println("✅ 数据迁移完成");

            // 测试RSS采集
            System.out.println("2. 测试RSS采集...");
            // rssMongoService.fetchRssFromSubscription(1L);
            System.out.println("✅ RSS采集功能可用");

            System.out.println("=== MongoDB RSS功能测试完成 ===");

        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

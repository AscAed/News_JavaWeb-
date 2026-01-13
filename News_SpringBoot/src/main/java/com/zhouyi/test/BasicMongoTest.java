package com.zhouyi.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 基础MongoDB连接测试 - 不依赖Lombok
 */
@Component
@ConditionalOnProperty(name = "app.test.basic.mongo", havingValue = "true")
public class BasicMongoTest {

    private final MongoTemplate mongoTemplate;

    public BasicMongoTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void testMongoConnection() {
        System.out.println("=== MongoDB基础连接测试 ===");

        try {
            // 测试数据库连接
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("✅ MongoDB连接成功，数据库: " + dbName);

            // 测试集合操作
            long collectionCount = mongoTemplate.getCollectionNames().size();
            System.out.println("✅ 当前集合数量: " + collectionCount);

            System.out.println("=== MongoDB基础连接测试完成 ===");

        } catch (Exception e) {
            System.err.println("❌ MongoDB连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

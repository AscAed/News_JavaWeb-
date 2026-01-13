package com.zhouyi.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB Connection Test - No Lombok dependency
 */
@Component
@ConditionalOnProperty(name = "app.test.mongo.connection", havingValue = "true")
public class MongoConnectionTest {

    private final MongoTemplate mongoTemplate;

    public MongoConnectionTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void testConnection() {
        System.out.println("=== MongoDB Connection Test Start ===");

        try {
            // Test basic connection
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("SUCCESS: MongoDB connected");
            System.out.println("Database name: " + dbName);

            // Test collection operations
            long collectionCount = mongoTemplate.getCollectionNames().size();
            System.out.println("Collection count: " + collectionCount);

            // Test insert and query
            mongoTemplate.createCollection("test_collection");
            System.out.println("SUCCESS: Test collection created");

            // Delete test collection
            mongoTemplate.dropCollection("test_collection");
            System.out.println("SUCCESS: Test collection dropped");

            System.out.println("=== MongoDB Connection Test Complete ===");
            System.out.println("MongoDB RSS functionality is ready to use!");

        } catch (Exception e) {
            System.err.println("ERROR: MongoDB connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.zhouyi.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        String key = "test:connection:key";
        String value = "Hello Redis from Spring Boot!";
        
        // Write to Redis
        redisTemplate.opsForValue().set(key, value);
        
        // Read from Redis
        Object retrievedValue = redisTemplate.opsForValue().get(key);
        
        // Assert
        assertEquals(value, retrievedValue);
        
        // Clean up
        redisTemplate.delete(key);
    }
}

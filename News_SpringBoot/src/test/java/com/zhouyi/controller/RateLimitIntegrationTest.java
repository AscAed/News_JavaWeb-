package com.zhouyi.controller;

import com.zhouyi.common.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试：验证 Redis 限流切面
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:ratelimittestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.data.elasticsearch.enabled=false"
})
public class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private ValueOperations<String, String> valueOperations;

    @Test
    void testRateLimitTriggered() throws Exception {
        // 1. 模拟 Redis 计数器超过限制 (假设限制为 5 次)
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(6L); // 返回 6，触发限流

        // 2. 准备用户信息
        CustomUserDetails userDetails = new CustomUserDetails(1, "testuser", "pass", 
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        // 3. 执行被 @RateLimit 标记的方法 (发送验证码)
        mockMvc.perform(post("/api/v1/auth/send-code")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\", \"captchaToken\":\"dummy\"}")
                .with(user(userDetails)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.code").value(429)) 
                .andExpect(jsonPath("$.message").value("操作过于频繁，请稍后再试"));
    }

    @Test
    void testRateLimitNotTriggered() throws Exception {
        // 1. 模拟 Redis 计数器未超过限制
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L); 

        // 2. 准备用户信息
        CustomUserDetails userDetails = new CustomUserDetails(1, "testuser", "pass", 
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        // 3. 执行发送验证码请求
        mockMvc.perform(post("/api/v1/auth/send-code")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\", \"captchaToken\":\"dummy\"}")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(429)));
    }
}

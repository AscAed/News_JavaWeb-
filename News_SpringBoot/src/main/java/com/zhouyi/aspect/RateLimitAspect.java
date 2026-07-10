package com.zhouyi.aspect;

import com.zhouyi.annotation.RateLimit;
import com.zhouyi.common.exception.RateLimitException;
import com.zhouyi.common.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 限流切面 - 基于 Redis 实现分布式限流
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private RedisScript<Long> limitScript;

    @PostConstruct
    public void init() {
        String script = "local count = redis.call('incr', KEYS[1]) " +
                       "if tonumber(count) == 1 then " +
                       "  redis.call('expire', KEYS[1], ARGV[1]) " +
                       "end " +
                       "return count";
        limitScript = new DefaultRedisScript<>(script, Long.class);
    }

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint joinPoint, RateLimit rateLimit) {
        String key = rateLimit.key();
        int count = rateLimit.count();
        int period = rateLimit.period();
        
        // 1. 构建唯一的限流 Key
        String combinedKey = buildKey(joinPoint, rateLimit);
        
        // 2. 使用 Lua 脚本进行原子计数与过期设置
        Long currentCount = redisTemplate.execute(limitScript, Collections.singletonList(combinedKey), String.valueOf(period));
        
        if (currentCount != null && currentCount > count) {
            log.warn("Rate limit exceeded for key: {}, count: {}", combinedKey, currentCount);
            
            // 【服务治理】：记录限流触发事件到 Redis，供管理员后台展现
            logRateLimitHit(combinedKey, rateLimit);
            
            throw new RateLimitException("操作过于频繁，请稍后再试");
        }
    }

    private void logRateLimitHit(String key, RateLimit rateLimit) {
        try {
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("timestamp", System.currentTimeMillis());
            logEntry.put("key", key);
            logEntry.put("ip", getClientIpAddress());
            logEntry.put("userId", getCurrentUserId());
            logEntry.put("jti", getCurrentJti());
            logEntry.put("period", rateLimit.period());
            logEntry.put("limit", rateLimit.count());
            
            String logJson = objectMapper.writeValueAsString(logEntry);
            // 推送到 Redis 列表，保留最近 100 条记录
            redisTemplate.opsForList().leftPush("sys:governance:ratelimit:logs", logJson);
            redisTemplate.opsForList().trim("sys:governance:ratelimit:logs", 0, 99);
        } catch (Exception e) {
            log.error("Failed to log rate limit hit: {}", e.getMessage());
        }
    }

    private String buildKey(JoinPoint joinPoint, RateLimit rateLimit) {
        StringBuilder sb = new StringBuilder(rateLimit.key());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 包含类名与方法名，确保不同接口的限流 Key 隔离
        sb.append(signature.getDeclaringTypeName()).append(":").append(signature.getName()).append(":");
        
        RateLimit.LimitType limitType = rateLimit.limitType();
        if (limitType == RateLimit.LimitType.GLOBAL) {
            sb.append("global");
        } else {
            // 【核心亮点】：动态维度降级限流
            // 1. 优先尝试从 Security 上下文获取已登录用户唯一 ID (防止单用户多 IP 恶意刷票)
            Integer userId = getCurrentUserId();
            if (userId != -1) {
                sb.append("user:").append(userId);
            } else {
                // 2. 若用户未登录 (游客状态)，则降级到 IP 维度进行限流
                // 此处 getClientIpAddress 已处理 X-Forwarded-For 以配合 Nginx 负载均衡
                sb.append("ip:").append(getClientIpAddress());
            }
        }
        
        return sb.toString();
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return -1;
    }

    private String getCurrentJti() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getJti();
        }
        return null;
    }

    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "unknown";
        HttpServletRequest request = attributes.getRequest();
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

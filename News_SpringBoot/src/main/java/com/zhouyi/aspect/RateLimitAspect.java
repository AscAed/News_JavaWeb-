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

/**
 * 限流切面 - 基于 Redis 实现分布式限流
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

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
            throw new RateLimitException("操作过于频繁，请稍后再试");
        }
    }

    private String buildKey(JoinPoint joinPoint, RateLimit rateLimit) {
        StringBuilder sb = new StringBuilder(rateLimit.key());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        sb.append(signature.getDeclaringTypeName()).append(":").append(signature.getName()).append(":");
        
        RateLimit.LimitType limitType = rateLimit.limitType();
        if (limitType == RateLimit.LimitType.IP) {
            sb.append(getClientIpAddress());
        } else if (limitType == RateLimit.LimitType.USER) {
            sb.append(getCurrentUserId());
        } else {
            sb.append("global");
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

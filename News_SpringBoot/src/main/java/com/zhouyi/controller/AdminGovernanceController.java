package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后台管理 - 服务治理与管控
 */
@Tag(name = "后台管理-服务治理")
@RestController
@RequestMapping("/api/v1/admin/governance")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGovernanceController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "获取限流触发日志")
    @GetMapping("/ratelimit/logs")
    public Result<List<String>> getRateLimitLogs() {
        List<String> logs = redisTemplate.opsForList().range("sys:governance:ratelimit:logs", 0, -1);
        return Result.success(logs);
    }

    @Operation(summary = "清除限流日志")
    @DeleteMapping("/ratelimit/logs")
    public Result<String> clearRateLimitLogs() {
        redisTemplate.delete("sys:governance:ratelimit:logs");
        return Result.success("日志已清除");
    }

    @Operation(summary = "获取服务治理统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getGovernanceStats() {
        Long rateLimitHits = redisTemplate.opsForList().size("sys:governance:ratelimit:logs");
        // 获取黑名单中的 Token 数量 (假设以 jwt:blacklist: 开头)
        var keys = redisTemplate.keys("jwt:blacklist:*");
        int blacklistedCount = keys != null ? keys.size() : 0;

        return Result.success(Map.of(
            "rateLimitHits", rateLimitHits != null ? rateLimitHits : 0,
            "blacklistedSessions", blacklistedCount,
            "systemStatus", "Healthy"
        ));
    }

    @Operation(summary = "强制将用户会话加入黑名单 (踢出登录)")
    @PostMapping("/blacklist")
    public Result<String> addToBlacklist(@RequestBody Map<String, String> params) {
        String token = params.get("token");
        String jti = params.get("jti");
        
        if (jti == null && token != null) {
            jti = jwtUtil.getJtiFromToken(token);
        }
        
        if (jti == null) {
            return Result.error("无法识别会话 (缺少 JTI)");
        }

        // 将 JTI 加入 Redis 黑名单，有效期设为 Token 剩余时长 (此处简单设为 24 小时)
        redisTemplate.opsForValue().set("jwt:blacklist:" + jti, "blocked", 24, TimeUnit.HOURS);
        
        return Result.success("会话已成功撤回 (已踢出)");
    }
}

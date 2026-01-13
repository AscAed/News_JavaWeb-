package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的认证测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "认证测试", description = "测试认证和权限")
public class AuthTestController {

    /**
     * 测试基本认证 - 不需要权限
     */
    @GetMapping("/public")
    @Operation(summary = "公开接口", description = "不需要认证")
    public Result<String> publicEndpoint() {
        return Result.success("公开接口访问成功");
    }

    /**
     * 测试认证 - 需要登录但不需要特殊权限
     */
    @GetMapping("/authenticated")
    @Operation(summary = "认证接口", description = "需要登录")
    public Result<Map<String, Object>> authenticatedEndpoint() {
        Map<String, Object> result = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            result.put("authenticated", auth.isAuthenticated());
            result.put("username", auth.getName());
            result.put("authorities", auth.getAuthorities().stream().map(Object::toString).toList());
        } else {
            result.put("authenticated", false);
            result.put("message", "未找到认证信息");
        }

        return Result.success(result);
    }

    /**
     * 测试管理员权限 - 需要ADMIN角色
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理员接口", description = "需要ADMIN角色")
    public Result<String> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Result.success("管理员接口访问成功，用户: " + auth.getName());
    }
}

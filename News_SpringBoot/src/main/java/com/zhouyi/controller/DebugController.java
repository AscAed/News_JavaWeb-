package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.service.UserRoleService;
import com.zhouyi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 调试控制器 - 用于检查认证和权限
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/debug")
@Tag(name = "调试接口", description = "用于调试认证和权限问题")
public class DebugController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 检查当前用户认证信息
     */
    @GetMapping("/auth")
    @Operation(summary = "检查认证信息", description = "获取当前用户的认证和权限信息")
    public Result<Map<String, Object>> checkAuth() {
        Map<String, Object> result = new HashMap<>();

        // 获取当前认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            result.put("authenticated", false);
            result.put("message", "未认证");
            return Result.success(result);
        }

        result.put("authenticated", auth.isAuthenticated());
        result.put("username", auth.getName());
        result.put("authorities", auth.getAuthorities().stream().map(Object::toString).toList());
        result.put("principal", auth.getPrincipal().toString());

        // 如果用户已认证，查询数据库中的角色信息
        if (auth.isAuthenticated()) {
            try {
                var userResult = userService.getUserByPhone(auth.getName());
                if (userResult.isSuccess() && userResult.getData() != null) {
                    Integer userId = userResult.getData().getId();
                    result.put("userId", userId);

                    var rolesResult = userRoleService.getRolesDetailsByUserId(userId);
                    if (rolesResult.isSuccess() && rolesResult.getData() != null) {
                        result.put("dbRoles", rolesResult.getData());
                    } else {
                        result.put("dbRoles", "查询失败或无角色");
                    }
                } else {
                    result.put("dbUser", "用户不存在");
                }
            } catch (Exception e) {
                result.put("dbError", e.getMessage());
            }
        }

        return Result.success(result);
    }

    /**
     * 检查RSS权限
     */
    @GetMapping("/rss-permission")
    @Operation(summary = "检查RSS权限", description = "检查当前用户是否有RSS操作权限")
    public Result<Map<String, Object>> checkRssPermission() {
        Map<String, Object> result = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            result.put("hasRssPermission", false);
            result.put("reason", "未认证");
            return Result.success(result);
        }

        boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        result.put("hasRssPermission", hasAdminRole);
        result.put("username", auth.getName());
        result.put("authorities", auth.getAuthorities().stream().map(Object::toString).toList());

        if (!hasAdminRole) {
            result.put("reason", "缺少ADMIN角色");
        }

        return Result.success(result);
    }
}

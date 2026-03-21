package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HealthCheckDTO;
import com.zhouyi.dto.SystemConfigDTO;
import com.zhouyi.service.HealthCheckService;
import com.zhouyi.service.OperationLogService;
import com.zhouyi.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "系统管理", description = "系统配置、日志和健康检查相关接口")
public class AdminController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private HealthCheckService healthCheckService;

    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取系统配置", description = "根据分类获取系统配置信息")
    @com.zhouyi.annotation.LogOperation(operationType = "READ", resourceType = "CONFIG", description = "获取系统配置")
    public Result<SystemConfigDTO> getSystemConfig(
            @Parameter(description = "配置分类：system, upload, security") @RequestParam(required = false) String category,
            HttpServletRequest request) {

        SystemConfigDTO config = systemConfigService.getSystemConfig(category);
        return Result.success(config);
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新系统配置", description = "批量更新系统配置")
    @com.zhouyi.annotation.LogOperation(operationType = "UPDATE", resourceType = "CONFIG", description = "更新系统配置")
    public Result<Map<String, Object>> updateSystemConfig(
            @RequestBody Map<String, String> configs,
            HttpServletRequest request) {

        Map<String, Object> result = systemConfigService.updateSystemConfig(configs);
        return Result.successWithMessageAndData("配置更新成功", result);
    }

    /**
     * 获取操作日志
     */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取操作日志", description = "分页获取系统操作日志")
    @com.zhouyi.annotation.LogOperation(operationType = "READ", resourceType = "LOG", description = "查询操作日志")
    public Result<com.zhouyi.dto.OperationLogDTO.PageResult> getOperationLogs(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量，最大100") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "用户ID筛选") @RequestParam(required = false) Integer userId,
            @Parameter(description = "操作类型筛选") @RequestParam(required = false) String action,
            @Parameter(description = "开始日期") @RequestParam(required = false) String dateFrom,
            @Parameter(description = "结束日期") @RequestParam(required = false) String dateTo,
            HttpServletRequest request) {

        // 限制每页最大数量
        if (pageSize > 100) {
            pageSize = 100;
        }

        com.zhouyi.dto.OperationLogDTO.PageResult logs = operationLogService.getOperationLogs(
                page, pageSize, userId, action, dateFrom, dateTo);

        return Result.success(logs);
    }

    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查系统各组件健康状态")
    public Result<HealthCheckDTO> healthCheck(HttpServletRequest request) {
        HealthCheckDTO health = healthCheckService.performHealthCheck();
        return Result.success(health);
    }
}

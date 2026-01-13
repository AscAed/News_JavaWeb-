package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HealthCheckDTO;
import com.zhouyi.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "健康检查", description = "系统健康状态检查接口")
public class HealthController {

    @Autowired
    private HealthCheckService healthCheckService;

    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查系统各组件健康状态，无需认证")
    public Result<HealthCheckDTO> healthCheck() {
        try {
            HealthCheckDTO health = healthCheckService.performHealthCheck();

            // 根据健康状态返回不同的HTTP状态码
            if ("unhealthy".equals(health.getStatus())) {
                return Result.error("系统不健康");
            } else if ("warning".equals(health.getStatus())) {
                return Result.successWithMessageAndData("系统健康状态警告", health);
            } else {
                return Result.success(health);
            }
        } catch (Exception e) {
            return Result.error("健康检查失败：" + e.getMessage());
        }
    }
}

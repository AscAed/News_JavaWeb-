package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.service.RssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * RSS订阅相关控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rss-subscriptions")
@Tag(name = "RSS管理", description = "RSS订阅源及采集管理")
public class RssSubscriptionController {

    @Autowired
    private RssService rssService;

    /**
     * 采集RSS内容
     *
     * @param id 订阅源ID
     * @return 采集结果
     */
    @PostMapping("/{id}/fetch")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "RSS内容采集", description = "手动触发RSS订阅源的内容采集")
    public Result<Map<String, Object>> fetchRssContent(
            @Parameter(description = "订阅源ID") @PathVariable Long id) {
        try {
            log.info("Request to fetch RSS subscription: {}", id);
            Map<String, Object> result = rssService.fetchAndSave(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to fetch RSS: {}", e.getMessage(), e);
            return Result.error("RSS内容采集失败: " + e.getMessage());
        }
    }

    /**
     * 创建 RSS 订阅源
     *
     * @param createDTO 创建参数
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建RSS订阅源", description = "添加新的RSS订阅源")
    public Result<Map<String, Object>> createSubscription(
            @org.springframework.validation.annotation.Validated @RequestBody com.zhouyi.dto.RssSubscriptionCreateDTO createDTO) {
        try {
            log.info("Request to create RSS subscription: {}", createDTO.getName());
            Map<String, Object> result = rssService.createSubscription(createDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to create RSS subscription: {}", e.getMessage(), e);
            return Result.error("RSS订阅源创建失败: " + e.getMessage());
        }
    }
}

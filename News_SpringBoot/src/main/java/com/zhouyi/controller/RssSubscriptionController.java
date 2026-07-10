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

import java.util.List;
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
        log.info("Request to fetch RSS subscription: {}", id);
        Map<String, Object> result = rssService.fetchAndSave(id);
        return Result.success(result);
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
        log.info("Request to create RSS subscription: {}", createDTO.getName());
        Map<String, Object> result = rssService.createSubscription(createDTO);
        return Result.success(result);
    }

    /**
     * 获取所有启用的订阅源列表
     *
     * @return 订阅源列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取启用的订阅源列表", description = "供前端展示新闻源分类筛选框使用")
    public Result<List<com.zhouyi.entity.RssSubscription>> listActiveSubscriptions() {
        List<com.zhouyi.entity.RssSubscription> list = rssService.listActiveSubscriptions();
        return Result.success(list);
    }
}

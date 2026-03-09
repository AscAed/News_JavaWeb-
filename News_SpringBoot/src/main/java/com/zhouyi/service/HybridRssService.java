package com.zhouyi.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Hybrid RSS服务，主要用于异步请求包装
 */
public interface HybridRssService {
    CompletableFuture<Map<String, Object>> fetchAndSave(Long subscriptionId, String section);
}

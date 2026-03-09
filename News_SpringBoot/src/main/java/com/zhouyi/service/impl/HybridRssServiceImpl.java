package com.zhouyi.service.impl;

import com.zhouyi.service.HybridRssService;
import com.zhouyi.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Hybrid RSS服务实现
 */
@Service
public class HybridRssServiceImpl implements HybridRssService {

    @Autowired
    private RssService rssService;

    @Async
    @Override
    public CompletableFuture<Map<String, Object>> fetchAndSave(Long subscriptionId, String section) {
        try {
            Map<String, Object> result = rssService.fetchAndSave(subscriptionId, section);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}

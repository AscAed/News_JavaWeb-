package com.zhouyi.controller.mongo;

import com.zhouyi.entity.mongo.RssArticle;
import com.zhouyi.entity.mongo.RssCategory;
import com.zhouyi.entity.mongo.RssSubscription;
import com.zhouyi.service.mongo.RssMongoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RSS MongoDB控制器
 */
@RestController
@RequestMapping("/api/v1/mongo")
public class RssMongoController {

    private static final Logger log = LoggerFactory.getLogger(RssMongoController.class);

    @Autowired
    private RssMongoService rssMongoService;

    /**
     * 手动触发RSS内容采集到MongoDB
     */
    @PostMapping("/rss-subscriptions/{id}/fetch")
    public ResponseEntity<Map<String, Object>> fetchRssToMongo(@PathVariable String id) {
        try {
            Map<String, Object> result = rssMongoService.fetchAndSaveToMongo(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "RSS内容采集到MongoDB完成");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("RSS采集到MongoDB失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "RSS采集失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取所有RSS订阅源
     */
    @GetMapping("/rss-subscriptions")
    public ResponseEntity<Map<String, Object>> getAllSubscriptions() {
        try {
            List<RssSubscription> subscriptions = rssMongoService.getAllEnabledSubscriptions();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取订阅源列表成功");
            response.put("data", subscriptions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取订阅源列表失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取订阅源列表失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据订阅源获取文章
     */
    @GetMapping("/rss-subscriptions/{subscriptionId}/articles")
    public ResponseEntity<Map<String, Object>> getArticlesBySubscription(
            @PathVariable String subscriptionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<RssArticle> articles = rssMongoService.getArticlesBySubscription(subscriptionId);

            // 简单分页处理
            int start = page * size;
            int end = Math.min(start + size, articles.size());
            List<RssArticle> pageArticles = articles.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取文章列表成功");
            response.put("data", Map.of(
                    "articles", pageArticles,
                    "total", articles.size(),
                    "page", page,
                    "size", size,
                    "totalPages", (int) Math.ceil((double) articles.size() / size)
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取文章列表失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据分类获取文章
     */
    @GetMapping("/categories/{category}/articles")
    public ResponseEntity<Map<String, Object>> getArticlesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<RssArticle> articles = rssMongoService.getArticlesByCategory(category);

            // 简单分页处理
            int start = page * size;
            int end = Math.min(start + size, articles.size());
            List<RssArticle> pageArticles = articles.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取分类文章成功");
            response.put("data", Map.of(
                    "articles", pageArticles,
                    "total", articles.size(),
                    "page", page,
                    "size", size,
                    "totalPages", (int) Math.ceil((double) articles.size() / size)
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取分类文章失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取分类文章失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 搜索文章
     */
    @GetMapping("/articles/search")
    public ResponseEntity<Map<String, Object>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<RssArticle> articles = rssMongoService.searchArticles(keyword);

            // 简单分页处理
            int start = page * size;
            int end = Math.min(start + size, articles.size());
            List<RssArticle> pageArticles = articles.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "搜索文章成功");
            response.put("data", Map.of(
                    "articles", pageArticles,
                    "total", articles.size(),
                    "page", page,
                    "size", size,
                    "totalPages", (int) Math.ceil((double) articles.size() / size),
                    "keyword", keyword
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索文章失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "搜索文章失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取热门分类
     */
    @GetMapping("/categories/hot")
    public ResponseEntity<Map<String, Object>> getHotCategories(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<RssCategory> categories = rssMongoService.getHotCategories(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取热门分类成功");
            response.put("data", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取热门分类失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取热门分类失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = rssMongoService.getArticleStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取统计信息成功");
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取统计信息失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 清理过期文章
     */
    @DeleteMapping("/articles/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldArticles(
            @RequestParam String beforeDate) {
        try {
            // 这里应该解析日期字符串，为了简化直接使用当前时间减去指定天数
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(beforeDate);
            long cleanedCount = rssMongoService.cleanupOldArticles(dateTime);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "清理过期文章成功");
            response.put("data", Map.of(
                    "cleaned_count", cleanedCount,
                    "before_date", beforeDate
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("清理过期文章失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "清理过期文章失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
}

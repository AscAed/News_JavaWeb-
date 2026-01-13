package com.zhouyi.controller.mongo;

import com.zhouyi.service.mongo.DataMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据迁移控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/migration")
public class DataMigrationController {

    @Autowired
    private DataMigrationService dataMigrationService;

    /**
     * 迁移RSS数据从MySQL到MongoDB
     */
    @PostMapping("/rss-to-mongo")
    public ResponseEntity<Map<String, Object>> migrateRssToMongo() {
        try {
            log.info("开始执行RSS数据迁移");

            Map<String, Object> result = dataMigrationService.migrateAllData();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据迁移执行完成");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("RSS数据迁移失败", e);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "数据迁移失败: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取迁移状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMigrationStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取迁移状态成功");
            response.put("data", Map.of(
                    "status", "ready",
                    "message", "数据迁移服务已就绪"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取迁移状态失败", e);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取迁移状态失败: " + e.getMessage());
            response.put("data", null);

            return ResponseEntity.status(500).body(response);
        }
    }
}

package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sync", description = "数据同步管理")
@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private com.zhouyi.service.NewsSyncService newsSyncService;

    @Operation(summary = "全量重建ES索引", description = "将MySQL和MongoDB中的历史数据同步到Elasticsearch中")
    @PostMapping("/es")
    public Result<String> syncToEs() {
        return newsSyncService.fullSync();
    }
}

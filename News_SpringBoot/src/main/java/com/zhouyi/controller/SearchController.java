package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.SearchResultDTO;
import com.zhouyi.service.NewsSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Search", description = "搜索相关接口")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private NewsSearchService newsSearchService;

    @Operation(summary = "全局新闻搜索", description = "基于Elasticsearch的全文检索，包含标题和内容匹配及高亮显示")
    @GetMapping("/news")
    public Result<SearchResultDTO> search(@RequestParam String keyword,
                                         @RequestParam(required = false) Integer type,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        SearchResultDTO result = newsSearchService.globalSearch(keyword, type, page, pageSize);
        return Result.success(result);
    }
}

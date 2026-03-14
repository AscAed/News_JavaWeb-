package com.zhouyi.service;

import com.zhouyi.dto.SearchResultDTO;

public interface NewsSearchService {
    /**
     * 全局搜索新闻（包含高亮效果，支持分页）
     * @param keyword 关键词
     * @param typeId 新闻类型ID (可选)
     * @param page 页码 (1-indexed)
     * @param pageSize 每页大小
     * @return 搜索结果包装对象
     */
    SearchResultDTO globalSearch(String keyword, Integer typeId, Integer page, Integer pageSize);
}

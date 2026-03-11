package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.NewsTypeCreateDTO;
import com.zhouyi.dto.NewsTypeStatusDTO;
import com.zhouyi.dto.NewsTypeUpdateDTO;
import com.zhouyi.entity.NewsType;
import com.zhouyi.mapper.NewsTypeMapper;
import com.zhouyi.service.NewsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻分类服务实现
 */
@Service
@Transactional
public class NewsTypeServiceImpl implements NewsTypeService {

    @Autowired
    private NewsTypeMapper newsTypeMapper;

    @Override
    @Cacheable(value = "categories", key = "#status + '_' + #sourceType + '_' + #sourceId", sync = true)
    public Result<Map<String, Object>> getCategories(Integer status, String sortBy, String sortOrder, String sourceType,
                                                     String sourceId) {
        try {
            // 设置默认值
            if (status == null) {
                status = 1; // 默认只查询启用的分类
            }
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "sort_order";
            }
            if (sortOrder == null || sortOrder.isEmpty()) {
                sortOrder = "asc";
            }

            // 验证排序字段
            if (!List.of("sort_order", "created_time", "tname").contains(sortBy)) {
                sortBy = "sort_order";
            }

            // 验证排序方向
            if (!List.of("asc", "desc").contains(sortOrder.toLowerCase())) {
                sortOrder = "asc";
            }

            List<NewsType> categories = newsTypeMapper.findByStatus(status, sourceType, sourceId);
            long total = newsTypeMapper.countByStatus(status, sourceType, sourceId);

            Map<String, Object> data = new HashMap<>();
            data.put("total", total);
            data.put("items", categories);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "获取分类列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<NewsType> getCategoryById(Integer id) {
        try {
            if (id == null || id <= 0) {
                return Result.error(400, "分类ID不能为空且必须大于0");
            }

            NewsType category = newsTypeMapper.findById(id);
            if (category == null) {
                return Result.error(404, "分类不存在");
            }

            // 获取新闻数量
            long newsCount = newsTypeMapper.countNewsByType(id);
            category.setNewsCount((int) newsCount);

            return Result.success(category);
        } catch (Exception e) {
            return Result.error(500, "获取分类详情失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Result<NewsType> createCategory(NewsTypeCreateDTO createDTO) {
        try {
            // 检查分类名称是否已存在
            NewsType existingCategory = newsTypeMapper.findByName(createDTO.getName());
            if (existingCategory != null) {
                return Result.error(409, "分类名称已存在");
            }

            NewsType newsType = new NewsType();
            newsType.setTypeName(createDTO.getName());
            newsType.setDescription(createDTO.getDescription());
            newsType.setIconUrl(createDTO.getIcon());
            newsType.setColor(createDTO.getColor());
            newsType.setSourceType(createDTO.getSourceType());
            newsType.setSourceId(createDTO.getSourceId());
            newsType.setStatus(1); // 默认启用
            newsType.setCreatedTime(LocalDateTime.now());
            newsType.setUpdatedTime(LocalDateTime.now());

            // 设置排序值
            if (createDTO.getSortOrder() != null) {
                newsType.setSortOrder(createDTO.getSortOrder());
            } else {
                newsType.setSortOrder(newsTypeMapper.getNextSortOrder());
            }

            int result = newsTypeMapper.insert(newsType);
            if (result > 0) {
                return Result.success(newsType);
            } else {
                return Result.error(500, "分类创建失败");
            }
        } catch (Exception e) {
            return Result.error(500, "分类创建失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Result<Void> updateCategory(Integer id, NewsTypeUpdateDTO updateDTO) {
        try {
            if (id == null || id <= 0) {
                return Result.error(400, "分类ID不能为空且必须大于0");
            }

            NewsType existingCategory = newsTypeMapper.findById(id);
            if (existingCategory == null) {
                return Result.error(404, "分类不存在");
            }

            // 检查名称是否与其他分类重复
            NewsType duplicateCategory = newsTypeMapper.findByName(updateDTO.getName());
            if (duplicateCategory != null && !duplicateCategory.getId().equals(id)) {
                return Result.error(409, "分类名称已存在");
            }

            existingCategory.setTypeName(updateDTO.getName());
            existingCategory.setDescription(updateDTO.getDescription());
            existingCategory.setIconUrl(updateDTO.getIcon());
            existingCategory.setColor(updateDTO.getColor());
            existingCategory.setSourceType(updateDTO.getSourceType());
            existingCategory.setSourceId(updateDTO.getSourceId());
            existingCategory.setSortOrder(updateDTO.getSortOrder());
            existingCategory.setUpdatedTime(LocalDateTime.now());

            int result = newsTypeMapper.update(existingCategory);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error(500, "分类更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "分类更新失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Result<Void> updateCategoryStatus(Integer id, NewsTypeStatusDTO statusDTO) {
        try {
            if (id == null || id <= 0) {
                return Result.error(400, "分类ID不能为空且必须大于0");
            }

            NewsType existingCategory = newsTypeMapper.findById(id);
            if (existingCategory == null) {
                return Result.error(404, "分类不存在");
            }

            int result = newsTypeMapper.updateStatus(id, statusDTO.getStatus(), LocalDateTime.now());

            if (result > 0) {
                return Result.success("分类状态更新成功");
            } else {
                return Result.error(500, "分类状态更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "分类状态更新失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Result<Void> deleteCategory(Integer id) {
        try {
            if (id == null || id <= 0) {
                return Result.error(400, "分类ID不能为空且必须大于0");
            }

            NewsType existingCategory = newsTypeMapper.findById(id);
            if (existingCategory == null) {
                return Result.error(404, "分类不存在");
            }

            // 检查是否有新闻使用该分类
            long newsCount = newsTypeMapper.countNewsByType(id);
            if (newsCount > 0) {
                return Result.error(409, "该分类下还有新闻，无法删除");
            }

            int result = newsTypeMapper.softDelete(id, LocalDateTime.now());
            if (result > 0) {
                return Result.success("分类删除成功");
            } else {
                return Result.error(500, "分类删除失败");
            }
        } catch (Exception e) {
            return Result.error(500, "分类删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCategoryStatistics(Integer id, String dateFrom, String dateTo) {
        try {
            if (id == null || id <= 0) {
                return Result.error(400, "分类ID不能为空且必须大于0");
            }

            NewsType category = newsTypeMapper.findById(id);
            if (category == null) {
                return Result.error(404, "分类不存在");
            }

            // 这里应该实现统计逻辑，暂时返回基础信息
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("category_id", id);
            statistics.put("category_name", category.getTypeName());
            statistics.put("total_news", newsTypeMapper.countNewsByType(id));
            statistics.put("published_news", newsTypeMapper.countNewsByType(id)); // 暂时简化
            statistics.put("draft_news", 0); // 暂时简化
            statistics.put("total_views", 0); // 需要从其他表获取
            statistics.put("total_likes", 0); // 需要从其他表获取
            statistics.put("total_comments", 0); // 需要从其他表获取

            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(500, "获取分类统计失败: " + e.getMessage());
        }
    }
}

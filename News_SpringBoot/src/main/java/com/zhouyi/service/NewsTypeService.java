package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.NewsTypeCreateDTO;
import com.zhouyi.dto.NewsTypeStatusDTO;
import com.zhouyi.dto.NewsTypeUpdateDTO;
import com.zhouyi.entity.NewsType;

import java.util.Map;

/**
 * 新闻分类服务接口
 */
public interface NewsTypeService {

    /**
     * 获取分类列表
     *
     * @param status     分类状态
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @param sourceType 数据源类型
     * @param sourceId   数据源ID
     * @return 分类列表
     */
    Result<Map<String, Object>> getCategories(Integer status, String sortBy, String sortOrder, String sourceType,
                                              String sourceId);

    /**
     * 根据ID获取分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    Result<NewsType> getCategoryById(Integer id);

    /**
     * 创建分类
     * 
     * @param createDTO 分类创建DTO
     * @return 创建结果
     */
    Result<NewsType> createCategory(NewsTypeCreateDTO createDTO);

    /**
     * 更新分类
     *
     * @param id        分类ID
     * @param updateDTO 分类更新DTO
     * @return 更新结果
     */
    Result<Void> updateCategory(Integer id, NewsTypeUpdateDTO updateDTO);

    /**
     * 更新分类状态
     *
     * @param id        分类ID
     * @param statusDTO 状态更新DTO
     * @return 更新结果
     */
    Result<Void> updateCategoryStatus(Integer id, NewsTypeStatusDTO statusDTO);

    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 删除结果
     */
    Result<Void> deleteCategory(Integer id);

    /**
     * 获取分类统计信息
     *
     * @param id       分类ID
     * @param dateFrom 统计开始日期
     * @param dateTo   统计结束日期
     * @return 统计信息
     */
    Result<Map<String, Object>> getCategoryStatistics(Integer id, String dateFrom, String dateTo);
}

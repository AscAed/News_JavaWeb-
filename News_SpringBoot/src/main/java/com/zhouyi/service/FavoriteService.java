package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.dto.FavoriteUpdateNoteDTO;
import com.zhouyi.entity.Favorite;

import java.util.Map;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param categoryId 分类ID（可选）
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 分页收藏列表
     */
    Result<Map<String, Object>> getFavoritesByUser(Integer userId, Integer page, Integer pageSize, 
                                                   Integer categoryId, String sortBy, String sortOrder);
    
    /**
     * 获取收藏详情
     * @param favoriteId 收藏记录ID
     * @return 收藏详情
     */
    Result<Favorite> getFavoriteById(Integer favoriteId);
    
    /**
     * 添加收藏
     * @param favoriteCreateDTO 收藏创建DTO
     * @param userId 用户ID
     * @return 创建结果
     */
    Result<Favorite> addFavorite(FavoriteCreateDTO favoriteCreateDTO, Integer userId);
    
    /**
     * 取消收藏
     * @param favoriteId 收藏记录ID
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> removeFavorite(Integer favoriteId, Integer userId);
    
    /**
     * 更新收藏备注
     * @param favoriteId 收藏记录ID
     * @param favoriteUpdateNoteDTO 备注更新DTO
     * @param userId 用户ID
     * @return 更新结果
     */
    Result<Void> updateFavoriteNote(Integer favoriteId, FavoriteUpdateNoteDTO favoriteUpdateNoteDTO, Integer userId);
    
    /**
     * 获取新闻收藏数量
     * @param newsId 新闻ID
     * @return 收藏数量
     */
    Result<Map<String, Object>> getFavoriteCountByNews(Integer newsId);
    
    /**
     * 检查用户是否已收藏新闻
     * @param userId 用户ID
     * @param newsId 新闻ID
     * @return 收藏状态
     */
    Result<Map<String, Object>> checkFavoriteStatus(Integer userId, Integer newsId);
}

package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.FavoriteBatchDTO;

import java.util.List;
import java.util.Map;

/**
 * 收藏服务扩展接口
 */
public interface FavoriteServiceExtended extends FavoriteService {
    
    /**
     * 批量操作收藏
     * @param batchDTO 批量操作DTO
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Map<String, Object>> batchOperation(FavoriteBatchDTO batchDTO, Integer userId);
    
    /**
     * 批量添加收藏
     * @param headlineIds 新闻ID列表
     * @param note 收藏备注
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Map<String, Object>> batchAddFavorites(List<Integer> headlineIds, String note, Integer userId);
    
    /**
     * 批量删除收藏
     * @param headlineIds 新闻ID列表
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Map<String, Object>> batchRemoveFavorites(List<Integer> headlineIds, Integer userId);
}

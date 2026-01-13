package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.FavoriteBatchDTO;
import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.dto.FavoriteUpdateNoteDTO;
import com.zhouyi.entity.Favorite;
import com.zhouyi.entity.Headline;
import com.zhouyi.mapper.FavoriteMapper;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.service.FavoriteService;
import com.zhouyi.service.FavoriteServiceExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 收藏服务扩展实现类
 */
@Service
public class FavoriteServiceExtendedImpl implements FavoriteServiceExtended {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Override
    public Result<Map<String, Object>> getFavoritesByUser(Integer userId, Integer page, Integer pageSize,
            Integer categoryId, String sortBy, String sortOrder) {
        return favoriteService.getFavoritesByUser(userId, page, pageSize, categoryId, sortBy, sortOrder);
    }

    @Override
    public Result<Favorite> getFavoriteById(Integer favoriteId) {
        return favoriteService.getFavoriteById(favoriteId);
    }

    @Override
    public Result<Favorite> addFavorite(FavoriteCreateDTO favoriteCreateDTO, Integer userId) {
        return favoriteService.addFavorite(favoriteCreateDTO, userId);
    }

    @Override
    public Result<Void> removeFavorite(Integer favoriteId, Integer userId) {
        return favoriteService.removeFavorite(favoriteId, userId);
    }

    @Override
    public Result<Void> updateFavoriteNote(Integer favoriteId, FavoriteUpdateNoteDTO favoriteUpdateNoteDTO,
            Integer userId) {
        return favoriteService.updateFavoriteNote(favoriteId, favoriteUpdateNoteDTO, userId);
    }

    @Override
    public Result<Map<String, Object>> getFavoriteCountByNews(Integer newsId) {
        return favoriteService.getFavoriteCountByNews(newsId);
    }

    @Override
    public Result<Map<String, Object>> checkFavoriteStatus(Integer userId, Integer newsId) {
        return favoriteService.checkFavoriteStatus(userId, newsId);
    }

    @Override
    public Result<Map<String, Object>> batchOperation(FavoriteBatchDTO batchDTO, Integer userId) {
        try {
            // 参数校验
            if (batchDTO == null) {
                return Result.error(400, "批量操作参数不能为空");
            }

            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }

            if (!"add".equals(batchDTO.getAction()) && !"remove".equals(batchDTO.getAction())) {
                return Result.error(400, "操作类型只能是add或remove");
            }

            if (batchDTO.getHeadlineIds() == null || batchDTO.getHeadlineIds().isEmpty()) {
                return Result.error(400, "新闻ID列表不能为空");
            }

            if (batchDTO.getHeadlineIds().size() > 50) {
                return Result.error(400, "批量操作最多支持50个新闻ID");
            }

            // 根据操作类型执行相应的批量操作
            if ("add".equals(batchDTO.getAction())) {
                return batchAddFavorites(batchDTO.getHeadlineIds(), batchDTO.getNote(), userId);
            } else {
                return batchRemoveFavorites(batchDTO.getHeadlineIds(), userId);
            }

        } catch (Exception e) {
            return Result.error(500, "批量操作失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> batchAddFavorites(List<Integer> headlineIds, String note, Integer userId) {
        try {
            int successCount = 0;
            int failedCount = 0;
            List<Integer> failedIds = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Integer headlineId : headlineIds) {
                try {
                    // 验证新闻是否存在
                    Headline headline = headlineMapper.selectById(headlineId);
                    if (headline == null) {
                        failedCount++;
                        failedIds.add(headlineId);
                        errors.add("新闻" + headlineId + "不存在或已删除");
                        continue;
                    }

                    // 检查是否已收藏
                    Favorite existingFavorite = favoriteMapper.findByUserIdAndNewsId(userId, headlineId);
                    if (existingFavorite != null) {
                        failedCount++;
                        failedIds.add(headlineId);
                        errors.add("新闻" + headlineId + "已收藏");
                        continue;
                    }

                    // 创建收藏记录
                    Favorite favorite = new Favorite();
                    favorite.setUserId(userId);
                    favorite.setNewsId(headlineId);
                    favorite.setNote(note);
                    favorite.setCreatedTime(LocalDateTime.now());
                    favorite.setUpdatedTime(LocalDateTime.now());

                    int result = favoriteMapper.insert(favorite);
                    if (result > 0) {
                        successCount++;
                    } else {
                        failedCount++;
                        failedIds.add(headlineId);
                        errors.add("新闻" + headlineId + "收藏失败");
                    }

                } catch (Exception e) {
                    failedCount++;
                    failedIds.add(headlineId);
                    errors.add("新闻" + headlineId + "收藏异常: " + e.getMessage());
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success_count", successCount);
            data.put("failed_count", failedCount);
            data.put("failed_ids", failedIds);
            data.put("errors", errors);

            if (failedCount == 0) {
                return Result.success(data);
            } else if (successCount == 0) {
                return Result.error(400, "批量收藏全部失败", data);
            } else {
                return Result.successWithMessageAndData("批量操作完成，部分失败", data);
            }

        } catch (Exception e) {
            return Result.error(500, "批量添加收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> batchRemoveFavorites(List<Integer> headlineIds, Integer userId) {
        try {
            int successCount = 0;
            int failedCount = 0;
            List<Integer> failedIds = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Integer headlineId : headlineIds) {
                try {
                    // 查找收藏记录
                    Favorite existingFavorite = favoriteMapper.findByUserIdAndNewsId(userId, headlineId);
                    if (existingFavorite == null) {
                        failedCount++;
                        failedIds.add(headlineId);
                        errors.add("新闻" + headlineId + "未收藏");
                        continue;
                    }

                    // 删除收藏
                    int result = favoriteMapper.deleteByUserIdAndNewsId(userId, headlineId);
                    if (result > 0) {
                        successCount++;
                    } else {
                        failedCount++;
                        failedIds.add(headlineId);
                        errors.add("新闻" + headlineId + "取消收藏失败");
                    }

                } catch (Exception e) {
                    failedCount++;
                    failedIds.add(headlineId);
                    errors.add("新闻" + headlineId + "取消收藏异常: " + e.getMessage());
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success_count", successCount);
            data.put("failed_count", failedCount);
            data.put("failed_ids", failedIds);
            data.put("errors", errors);

            if (failedCount == 0) {
                return Result.success(data);
            } else if (successCount == 0) {
                return Result.error(400, "批量取消收藏全部失败", data);
            } else {
                return Result.successWithMessageAndData("批量操作完成，部分失败", data);
            }

        } catch (Exception e) {
            return Result.error(500, "批量删除收藏失败: " + e.getMessage());
        }
    }
}

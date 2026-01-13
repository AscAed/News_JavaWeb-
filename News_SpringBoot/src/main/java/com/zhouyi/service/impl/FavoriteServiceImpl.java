package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.dto.FavoriteUpdateNoteDTO;
import com.zhouyi.entity.Favorite;
import com.zhouyi.entity.Headline;
import com.zhouyi.mapper.FavoriteMapper;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private HeadlineMapper headlineMapper;
    
    @Override
    public Result<Map<String, Object>> getFavoritesByUser(Integer userId, Integer page, Integer pageSize, 
                                                         Integer categoryId, String sortBy, String sortOrder) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            page = page == null || page < 1 ? 1 : page;
            pageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
            sortBy = sortBy == null ? "created_time" : sortBy;
            sortOrder = sortOrder == null ? "desc" : sortOrder;
            
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 查询收藏列表
            List<Favorite> favorites = favoriteMapper.findByUserIdWithPage(userId, offset, pageSize);
            
            // 统计总数
            long total = favoriteMapper.countByUserId(userId);
            
            // 构建收藏详情列表
            List<Map<String, Object>> favoriteItems = new ArrayList<>();
            for (Favorite favorite : favorites) {
                Map<String, Object> favoriteItem = new HashMap<>();
                
                // 获取新闻详情
                Headline headline = headlineMapper.selectById(favorite.getNewsId());
                if (headline != null) {
                    Map<String, Object> headlineInfo = new HashMap<>();
                    headlineInfo.put("id", headline.getHid());
                    headlineInfo.put("title", headline.getTitle());
                    headlineInfo.put("summary", headline.getSummary());
                    headlineInfo.put("cover_image", headline.getCoverImage());
                    headlineInfo.put("published_time", headline.getPublishedTime());
                    headlineInfo.put("page_views", headline.getPageViews());
                    
                    // 作者信息（简化处理）
                    Map<String, Object> author = new HashMap<>();
                    author.put("id", headline.getPublisher());
                    author.put("username", headline.getAuthor());
                    headlineInfo.put("author", author);
                    
                    favoriteItem.put("headline", headlineInfo);
                }
                
                favoriteItem.put("id", favorite.getId());
                favoriteItem.put("note", favorite.getNote());
                favoriteItem.put("created_time", favorite.getCreatedTime());
                
                favoriteItems.add(favoriteItem);
            }
            
            // 构建返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("total", total);
            data.put("page", page);
            data.put("page_size", pageSize);
            data.put("total_pages", (int) Math.ceil((double) total / pageSize));
            data.put("items", favoriteItems);
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "获取收藏列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Favorite> getFavoriteById(Integer favoriteId) {
        try {
            if (favoriteId == null || favoriteId <= 0) {
                return Result.error(400, "收藏ID不能为空且必须为正数");
            }
            
            Favorite favorite = favoriteMapper.findById(favoriteId);
            if (favorite == null) {
                return Result.error(404, "收藏记录不存在");
            }
            
            return Result.success(favorite);
            
        } catch (Exception e) {
            return Result.error(500, "获取收藏详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Favorite> addFavorite(FavoriteCreateDTO favoriteCreateDTO, Integer userId) {
        try {
            // 参数校验
            if (favoriteCreateDTO == null) {
                return Result.error(400, "收藏信息不能为空");
            }
            
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            // 验证新闻是否存在
            Headline headline = headlineMapper.selectById(favoriteCreateDTO.getHeadlineId());
            if (headline == null) {
                return Result.error(404, "新闻不存在");
            }
            
            // 检查是否已收藏
            Favorite existingFavorite = favoriteMapper.findByUserIdAndNewsId(userId, favoriteCreateDTO.getHeadlineId());
            if (existingFavorite != null) {
                return Result.error(409, "该新闻已收藏");
            }
            
            // 创建收藏记录
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setNewsId(favoriteCreateDTO.getHeadlineId());
            favorite.setNote(favoriteCreateDTO.getNote());
            favorite.setCreatedTime(LocalDateTime.now());
            favorite.setUpdatedTime(LocalDateTime.now());
            
            // 保存收藏
            int result = favoriteMapper.insert(favorite);
            if (result <= 0) {
                return Result.error(500, "收藏失败");
            }
            
            return Result.success(favorite);
            
        } catch (Exception e) {
            return Result.error(500, "添加收藏失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> removeFavorite(Integer favoriteId, Integer userId) {
        try {
            if (favoriteId == null || favoriteId <= 0) {
                return Result.error(400, "收藏ID不能为空且必须为正数");
            }
            
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            // 查找收藏记录
            Favorite favorite = favoriteMapper.findById(favoriteId);
            if (favorite == null) {
                return Result.error(404, "收藏记录不存在");
            }
            
            // 验证权限：只能删除自己的收藏
            if (!favorite.getUserId().equals(userId)) {
                return Result.error(403, "无权限删除此收藏");
            }
            
            // 删除收藏
            int result = favoriteMapper.deleteById(favoriteId);
            if (result <= 0) {
                return Result.error(500, "取消收藏失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            return Result.error(500, "取消收藏失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> updateFavoriteNote(Integer favoriteId, FavoriteUpdateNoteDTO favoriteUpdateNoteDTO, Integer userId) {
        try {
            if (favoriteId == null || favoriteId <= 0) {
                return Result.error(400, "收藏ID不能为空且必须为正数");
            }
            
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            if (favoriteUpdateNoteDTO == null) {
                return Result.error(400, "更新内容不能为空");
            }
            
            // 查找收藏记录
            Favorite favorite = favoriteMapper.findById(favoriteId);
            if (favorite == null) {
                return Result.error(404, "收藏记录不存在");
            }
            
            // 验证权限：只能更新自己的收藏
            if (!favorite.getUserId().equals(userId)) {
                return Result.error(403, "无权限更新此收藏");
            }
            
            // 更新备注
            favorite.setNote(favoriteUpdateNoteDTO.getNote());
            favorite.setUpdatedTime(LocalDateTime.now());
            
            int result = favoriteMapper.updateNote(favorite);
            if (result <= 0) {
                return Result.error(500, "更新收藏备注失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            return Result.error(500, "更新收藏备注失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getFavoriteCountByNews(Integer newsId) {
        try {
            if (newsId == null || newsId <= 0) {
                return Result.error(400, "新闻ID不能为空且必须为正数");
            }
            
            // 验证新闻是否存在
            Headline headline = headlineMapper.selectById(newsId);
            if (headline == null) {
                return Result.error(404, "新闻不存在");
            }
            
            // 统计收藏数量
            long favoriteCount = favoriteMapper.countByNewsId(newsId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("headline_id", newsId);
            data.put("favorite_count", favoriteCount);
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "获取收藏数量失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> checkFavoriteStatus(Integer userId, Integer newsId) {
        try {
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            if (newsId == null || newsId <= 0) {
                return Result.error(400, "新闻ID不能为空且必须为正数");
            }
            
            // 检查收藏状态
            Favorite favorite = favoriteMapper.findByUserIdAndNewsId(userId, newsId);
            boolean isFavorited = favorite != null;
            
            Map<String, Object> data = new HashMap<>();
            data.put("headline_id", newsId);
            data.put("is_favorited", isFavorited);
            if (isFavorited && favorite != null) {
                data.put("favorite_id", favorite.getId());
                data.put("created_time", favorite.getCreatedTime());
            }
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "检查收藏状态失败: " + e.getMessage());
        }
    }
}

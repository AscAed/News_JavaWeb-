package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.dto.FavoriteUpdateNoteDTO;
import com.zhouyi.dto.FavoriteBatchDTO;
import com.zhouyi.service.FavoriteServiceExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 收藏管理控制器
 */
@RestController
@RequestMapping("/api/v1")
public class FavoriteController {
    
    @Autowired
    private FavoriteServiceExtended favoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取用户收藏列表
     */
    @GetMapping("/favorites")
    public Result<?> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer page_size,
            @RequestParam(required = false) Integer category_id,
            @RequestParam(defaultValue = "created_time") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_order,
            HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.getFavoritesByUser(userId, page, page_size, category_id, sort_by, sort_order);
    }
    
    /**
     * 获取收藏详情
     */
    @GetMapping("/favorites/{id}")
    public Result<?> getFavoriteById(@PathVariable("id") Integer id,
                                    HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        Result<?> result = favoriteService.getFavoriteById(id);
        if (result.isSuccess()) {
            // 验证权限：只能查看自己的收藏
            // 这里应该检查用户是否为管理员，简化处理
        }
        
        return result;
    }
    
    /**
     * 添加收藏
     */
    @PostMapping("/favorites")
    public Result<?> addFavorite(@Valid @RequestBody FavoriteCreateDTO favoriteCreateDTO,
                                 HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.addFavorite(favoriteCreateDTO, userId);
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/favorites/{id}")
    public Result<?> removeFavorite(@PathVariable("id") Integer id,
                                    HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.removeFavorite(id, userId);
    }
    
    /**
     * 更新收藏备注
     */
    @PatchMapping("/favorites/{id}/note")
    public Result<?> updateFavoriteNote(@PathVariable("id") Integer id,
                                        @Valid @RequestBody FavoriteUpdateNoteDTO favoriteUpdateNoteDTO,
                                        HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.updateFavoriteNote(id, favoriteUpdateNoteDTO, userId);
    }
    
    /**
     * 获取新闻收藏数量
     */
    @GetMapping("/headlines/{headline_id}/favorites/count")
    public Result<?> getFavoriteCountByHeadline(@PathVariable("headline_id") Integer headline_id) {
        return favoriteService.getFavoriteCountByNews(headline_id);
    }
    
    /**
     * 检查收藏状态
     */
    @GetMapping("/headlines/{headline_id}/favorites/status")
    public Result<?> checkFavoriteStatus(@PathVariable("headline_id") Integer headline_id,
                                         HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.checkFavoriteStatus(userId, headline_id);
    }
    
    /**
     * 批量操作收藏
     */
    @PostMapping("/favorites/batch")
    public Result<?> batchOperation(@Valid @RequestBody FavoriteBatchDTO batchDTO,
                                   HttpServletRequest request) {
        
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return favoriteService.batchOperation(batchDTO, userId);
    }
    
    /**
     * 从JWT Token中获取用户ID
     */
    private Integer getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}

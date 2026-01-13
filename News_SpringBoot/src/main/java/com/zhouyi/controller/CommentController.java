package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.dto.CommentUpdateDTO;
import com.zhouyi.dto.CommentStatusDTO;
import com.zhouyi.dto.CommentLikeDTO;
import com.zhouyi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 评论管理控制器
 */
@RestController
@RequestMapping("/api/v1")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取新闻评论列表
     */
    @GetMapping("/headlines/{headline_id}/comments")
    public Result<?> getCommentsByHeadline(
            @PathVariable("headline_id") Integer headlineId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer page_size,
            @RequestParam(defaultValue = "created_time") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_order,
            @RequestParam(required = false) Integer status) {
        
        return commentService.getCommentsByHeadline(headlineId, page, page_size, sort_by, sort_order, status);
    }
    
    /**
     * 获取评论详情
     */
    @GetMapping("/comments/{id}")
    public Result<?> getCommentById(@PathVariable("id") String id) {
        return commentService.getCommentById(id);
    }
    
    /**
     * 创建评论
     */
    @PostMapping("/comments")
    public Result<?> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                  HttpServletRequest request) {
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return commentService.createComment(commentCreateDTO, userId);
    }
    
    /**
     * 更新评论
     */
    @PutMapping("/comments/{id}")
    public Result<?> updateComment(@PathVariable("id") String id,
                                  @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
                                  HttpServletRequest request) {
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return commentService.updateComment(id, commentUpdateDTO, userId);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<?> deleteComment(@PathVariable("id") String id,
                                   HttpServletRequest request) {
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return commentService.deleteComment(id, userId);
    }
    
    /**
     * 更新评论状态
     */
    @PatchMapping("/comments/{id}/status")
    public Result<?> updateCommentStatus(@PathVariable("id") String id,
                                        @Valid @RequestBody CommentStatusDTO commentStatusDTO,
                                        HttpServletRequest request) {
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return commentService.updateCommentStatus(id, commentStatusDTO, userId);
    }
    
    /**
     * 点赞或取消点赞评论
     */
    @PostMapping("/comments/{id}/like")
    public Result<?> likeComment(@PathVariable("id") String id,
                                 @Valid @RequestBody CommentLikeDTO commentLikeDTO,
                                 HttpServletRequest request) {
        // 从JWT Token中获取用户ID
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return commentService.likeComment(id, commentLikeDTO, userId);
    }
    
    /**
     * 获取用户评论列表
     */
    @GetMapping("/users/{user_id}/comments")
    public Result<?> getCommentsByUser(
            @PathVariable("user_id") Integer user_id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer page_size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        
        // 从JWT Token中获取当前用户ID
        Integer currentUserId = getUserIdFromToken(request);
        if (currentUserId == null) {
            return Result.error(401, "未授权访问");
        }
        
        // 验证权限：只能查看自己的评论或管理员可以查看所有用户评论
        if (!currentUserId.equals(user_id)) {
            // 这里应该检查用户是否为管理员，简化处理
            return Result.error(403, "无权限查看其他用户的评论");
        }
        
        return commentService.getCommentsByUser(user_id, page, page_size, status);
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

package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.dto.CommentUpdateDTO;
import com.zhouyi.dto.CommentStatusDTO;
import com.zhouyi.dto.CommentLikeDTO;
import com.zhouyi.entity.mongodb.Comment;

import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 获取新闻评论列表
     * @param headlineId 新闻ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @param status 评论状态
     * @return 分页评论列表
     */
    Result<Map<String, Object>> getCommentsByHeadline(Integer headlineId, Integer page, Integer pageSize, 
                                                     String sortBy, String sortOrder, Integer status);
    
    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return 评论详情
     */
    Result<Comment> getCommentById(String commentId);
    
    /**
     * 创建评论
     * @param commentCreateDTO 评论创建DTO
     * @param userId 用户ID
     * @return 创建结果
     */
    Result<Comment> createComment(CommentCreateDTO commentCreateDTO, Integer userId);
    
    /**
     * 更新评论
     * @param commentId 评论ID
     * @param commentUpdateDTO 评论更新DTO
     * @param userId 用户ID
     * @return 更新结果
     */
    Result<Comment> updateComment(String commentId, CommentUpdateDTO commentUpdateDTO, Integer userId);
    
    /**
     * 删除评论（软删除）
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> deleteComment(String commentId, Integer userId);
    
    /**
     * 更新评论状态
     * @param commentId 评论ID
     * @param commentStatusDTO 状态更新DTO
     * @param userId 用户ID
     * @return 更新结果
     */
    Result<Void> updateCommentStatus(String commentId, CommentStatusDTO commentStatusDTO, Integer userId);
    
    /**
     * 点赞或取消点赞评论
     * @param commentId 评论ID
     * @param commentLikeDTO 点赞DTO
     * @param userId 用户ID
     * @return 点赞结果
     */
    Result<Map<String, Object>> likeComment(String commentId, CommentLikeDTO commentLikeDTO, Integer userId);
    
    /**
     * 获取用户评论列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param status 评论状态
     * @return 用户评论列表
     */
    Result<Map<String, Object>> getCommentsByUser(Integer userId, Integer page, Integer pageSize, Integer status);
}

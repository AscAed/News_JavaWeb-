package com.zhouyi.repository.mongodb;

import com.zhouyi.entity.mongodb.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论MongoDB仓库
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    /**
     * 根据新闻ID查找评论
     */
    List<Comment> findByNewsIdAndIsDeletedOrderByCreatedAtDesc(Integer newsId, Boolean isDeleted);
    
    /**
     * 根据用户ID查找评论
     */
    List<Comment> findByUserIdAndIsDeletedOrderByCreatedAtDesc(Integer userId, Boolean isDeleted);
    
    /**
     * 根据父评论ID查找回复
     */
    List<Comment> findByParentIdAndIsDeletedOrderByCreatedAtAsc(String parentId, Boolean isDeleted);
    
    /**
     * 查找顶级评论（无父评论）
     */
    List<Comment> findByNewsIdAndParentIdIsNullAndIsDeletedOrderByCreatedAtDesc(Integer newsId, Boolean isDeleted);
    
    /**
     * 根据新闻ID和状态查找顶级评论
     */
    List<Comment> findByNewsIdAndParentIdIsNullAndStatusOrderByCreatedAtDesc(Integer newsId, Integer status);
    
    /**
     * 根据用户ID和状态查找评论
     */
    List<Comment> findByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, Integer status);
    
    /**
     * 根据状态查找评论
     */
    List<Comment> findByStatus(Integer status);
    
    /**
     * 统计新闻评论数
     */
    long countByNewsIdAndIsDeleted(Integer newsId, Boolean isDeleted);
    
    /**
     * 统计用户评论数
     */
    long countByUserIdAndIsDeleted(Integer userId, Boolean isDeleted);
    
    /**
     * 查找指定时间后的评论
     */
    List<Comment> findByCreatedAtAfterAndIsDeletedOrderByCreatedAtDesc(LocalDateTime dateTime, Boolean isDeleted);
    
    /**
     * 查找热门评论（点赞数排序）
     */
    List<Comment> findByNewsIdAndIsDeletedOrderByLikeCountDesc(Integer newsId, Boolean isDeleted);
}

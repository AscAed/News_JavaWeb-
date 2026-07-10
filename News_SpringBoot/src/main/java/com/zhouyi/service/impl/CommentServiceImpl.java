package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.dto.CommentUpdateDTO;
import com.zhouyi.dto.CommentStatusDTO;
import com.zhouyi.dto.CommentLikeDTO;
import com.zhouyi.entity.mongodb.Comment;
import com.zhouyi.entity.User;
import com.zhouyi.repository.mongodb.CommentRepository;
import com.zhouyi.service.CommentService;
import com.zhouyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Autowired
    private com.zhouyi.mapper.HeadlineMapper headlineMapper;
    
    @Override
    public Result<Map<String, Object>> getCommentsByHeadline(Integer headlineId, Integer page, Integer pageSize, 
                                                           String sortBy, String sortOrder, Integer status) {
        try {
            // 参数校验
            if (headlineId == null || headlineId <= 0) {
                return Result.error(400, "新闻ID不能为空且必须为正数");
            }
            
            page = page == null || page < 1 ? 1 : page;
            pageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
            sortBy = sortBy == null ? "created_at" : sortBy;
            sortOrder = sortOrder == null ? "desc" : sortOrder;
            
            // 获取顶级评论总数以进行分页
            long total;
            if (status != null) {
                total = commentRepository.countByNewsIdAndParentIdIsNullAndStatus(headlineId, status);
            } else {
                total = commentRepository.countByNewsIdAndParentIdIsNullAndIsDeleted(headlineId, false);
            }
            
            // 分页处理
            int totalTopLevel = topComments.size();
            long totalGlobal = commentRepository.countByNewsIdAndIsDeleted(headlineId, false);
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalTopLevel);
            
            List<Comment> pagedComments = startIndex < totalTopLevel ? 
                topComments.subList(startIndex, endIndex) : new ArrayList<>();
            
            // 构建评论树结构
            List<Map<String, Object>> commentTrees = assembleCommentTree(headlineId, page, pageSize, status);
            
            // 构建返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("total", totalGlobal);
            data.put("page", page);
            data.put("page_size", pageSize);
            data.put("total_pages", (int) Math.ceil((double) totalTopLevel / pageSize));
            data.put("items", commentTrees);
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "获取评论列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Comment> getCommentById(String commentId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return Result.error(400, "评论ID不能为空");
            }
            
            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return Result.error(404, "评论不存在");
            }
            
            Comment comment = commentOpt.get();
            if (comment.getIsDeleted()) {
                return Result.error(404, "评论已删除");
            }
            
            return Result.success(comment);
            
        } catch (Exception e) {
            return Result.error(500, "获取评论详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Comment> createComment(CommentCreateDTO commentCreateDTO, Integer userId) {
        try {
            // 参数校验
            if (commentCreateDTO == null) {
                return Result.error(400, "评论信息不能为空");
            }
            
            // 验证用户存在
            Result<User> userResult = userService.getUserById(userId);
            if (!userResult.isSuccess()) {
                return Result.error(404, "用户不存在");
            }
            
            // 如果是回复评论，验证父评论存在且属于同一新闻
            if (commentCreateDTO.getParentId() != null && !commentCreateDTO.getParentId().trim().isEmpty()) {
                Optional<Comment> parentCommentOpt = commentRepository.findById(commentCreateDTO.getParentId());
                if (!parentCommentOpt.isPresent() || parentCommentOpt.get().getIsDeleted()) {
                    return Result.error(404, "父评论不存在");
                }
                
                // 安全检查：验证父评论是否属于同一新闻
                Comment parentComment = parentCommentOpt.get();
                if (!parentComment.getNewsId().equals(commentCreateDTO.getHeadlineId())) {
                    return Result.error(400, "非法操作：无法回复其他新闻的评论");
                }
            }
            
            // 创建评论对象
            Comment comment = new Comment();
            comment.setNewsId(commentCreateDTO.getHeadlineId());
            comment.setUserId(userId);
            
            // XSS安全处理：转义HTML标签
            String sanitizedContent = HtmlUtils.htmlEscape(commentCreateDTO.getContent());
            comment.setContent(sanitizedContent);
            comment.setParentId(commentCreateDTO.getParentId());
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setIsDeleted(false);
            comment.setStatus(1); // 默认显示状态
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            
            // 冗余用户信息
            User user = userResult.getData();
            Comment.UserInfo userInfo = new Comment.UserInfo();
            userInfo.setUsername(user.getUsername());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            comment.setUserInfo(userInfo);
            
            // 保存评论
            Comment savedComment = commentRepository.save(comment);
            
            // 如果是回复评论，更新父评论的回复数 (原子操作)
            if (comment.getParentId() != null) {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(comment.getParentId())
                );
                Update update = new Update().inc("reply_count", 1).set("updated_at", LocalDateTime.now());
                mongoTemplate.updateFirst(query, update, Comment.class);
            }

            // 更新 MySQL 中的新闻评论总数
            try {
                headlineMapper.incrementCommentCount(comment.getNewsId());
            } catch (Exception e) {
                // 记录日志但不影响主流程
                System.err.println("Failed to increment headline comment count: " + e.getMessage());
            }
            
            return Result.success(savedComment);
            
        } catch (Exception e) {
            return Result.error(500, "创建评论失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Comment> updateComment(String commentId, CommentUpdateDTO commentUpdateDTO, Integer userId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return Result.error(400, "评论ID不能为空");
            }
            
            if (commentUpdateDTO == null) {
                return Result.error(400, "更新内容不能为空");
            }
            
            // 查找评论
            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return Result.error(404, "评论不存在");
            }
            
            Comment comment = commentOpt.get();
            if (comment.getIsDeleted()) {
                return Result.error(404, "评论已删除");
            }
            
            // 验证权限：只有作者或管理员可以修改
            if (!comment.getUserId().equals(userId)) {
                // 这里应该检查用户是否为管理员，简化处理
                return Result.error(403, "无权限修改此评论");
            }
            
            // 更新评论内容 (XSS安全处理)
            String sanitizedContent = HtmlUtils.htmlEscape(commentUpdateDTO.getContent());
            comment.setContent(sanitizedContent);
            comment.setUpdatedAt(LocalDateTime.now());
            
            Comment updatedComment = commentRepository.save(comment);
            return Result.success(updatedComment);
            
        } catch (Exception e) {
            return Result.error(500, "更新评论失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> deleteComment(String commentId, Integer userId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return Result.error(400, "评论ID不能为空");
            }
            
            // 查找评论
            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return Result.error(404, "评论不存在");
            }
            
            Comment comment = commentOpt.get();
            if (comment.getIsDeleted()) {
                return Result.error(404, "评论已删除");
            }
            
            // 验证权限：只有作者或管理员可以删除
            if (!comment.getUserId().equals(userId)) {
                // 这里应该检查用户是否为管理员，简化处理
                return Result.error(403, "无权限删除此评论");
            }
            
            // 软删除评论
            comment.setIsDeleted(true);
            comment.setStatus(2); // 删除状态
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
            
            // 如果是回复评论，更新父评论的回复数 (原子操作)
            if (comment.getParentId() != null) {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(comment.getParentId()).and("reply_count").gt(0)
                );
                Update update = new Update().inc("reply_count", -1).set("updated_at", LocalDateTime.now());
                mongoTemplate.updateFirst(query, update, Comment.class);
            }

            // 更新 MySQL 中的新闻评论总数
            try {
                headlineMapper.decrementCommentCount(comment.getNewsId());
            } catch (Exception e) {
                System.err.println("Failed to decrement headline comment count: " + e.getMessage());
            }
            
            return Result.success();
            
        } catch (Exception e) {
            return Result.error(500, "删除评论失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> updateCommentStatus(String commentId, CommentStatusDTO commentStatusDTO, Integer userId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return Result.error(400, "评论ID不能为空");
            }
            
            if (commentStatusDTO == null) {
                return Result.error(400, "状态信息不能为空");
            }
            
            // 查找评论
            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return Result.error(404, "评论不存在");
            }
            
            Comment comment = commentOpt.get();
            if (comment.getIsDeleted()) {
                return Result.error(404, "评论已删除");
            }
            
            // 验证权限：只有管理员可以修改状态
            // 这里应该检查用户是否为管理员，简化处理
            
            // 更新评论状态
            comment.setStatus(commentStatusDTO.getStatus());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
            
            return Result.success();
            
        } catch (Exception e) {
            return Result.error(500, "更新评论状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Map<String, Object>> likeComment(String commentId, CommentLikeDTO commentLikeDTO, Integer userId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return Result.error(400, "评论ID不能为空");
            }
            
            if (commentLikeDTO == null) {
                return Result.error(400, "操作信息不能为空");
            }
            
            // 查找评论
            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return Result.error(404, "评论不存在");
            }
            
            Comment comment = commentOpt.get();
            if (comment.getIsDeleted()) {
                return Result.error(404, "评论已删除");
            }
            
            // 处理点赞逻辑（原子操作）
            boolean isLiked = "like".equals(commentLikeDTO.getAction());
            
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(commentId)
            );
            
            Update update = new Update().set("updated_at", LocalDateTime.now());
            if (isLiked) {
                update.inc("like_count", 1);
            } else {
                update.inc("like_count", -1);
                // 确保不出现负数点赞
                query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("like_count").gt(0));
            }
            
            mongoTemplate.updateFirst(query, update, Comment.class);
            
            // 重新获取更新后的评论以返回最新的点赞数
            Comment updatedComment = commentRepository.findById(commentId).orElse(comment);
            
            // 构建返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("id", commentId);
            data.put("like_count", updatedComment.getLikeCount());
            data.put("is_liked", isLiked);
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "点赞操作失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getCommentsByUser(Integer userId, Integer page, Integer pageSize, Integer status) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID不能为空且必须为正数");
            }
            
            page = page == null || page < 1 ? 1 : page;
            pageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
            
            // 查询用户评论
            List<Comment> comments;
            if (status != null) {
                comments = commentRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
            } else {
                comments = commentRepository.findByUserIdAndIsDeletedOrderByCreatedAtDesc(userId, false);
            }
            
            // 分页处理
            int total = comments.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Comment> pagedComments = startIndex < total ? 
                comments.subList(startIndex, endIndex) : new ArrayList<>();
            
            // 构建返回结果
            List<Map<String, Object>> commentList = new ArrayList<>();
            for (Comment comment : pagedComments) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("id", comment.getId());
                commentMap.put("content", comment.getContent());
                commentMap.put("newsId", comment.getNewsId());
                commentMap.put("likeCount", comment.getLikeCount());
                commentMap.put("replyCount", comment.getReplyCount());
                commentMap.put("status", comment.getStatus());
                commentMap.put("createdAt", comment.getCreatedAt());
                commentMap.put("userInfo", comment.getUserInfo());
                commentList.add(commentMap);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("total", total);
            data.put("page", page);
            data.put("page_size", pageSize);
            data.put("items", commentList);
            
            return Result.success(data);
            
        } catch (Exception e) {
            return Result.error(500, "获取用户评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 实现线性的 O(N) 评论树组装算法（匹配论文 5.6.1 节描述）
     */
    private List<Map<String, Object>> assembleCommentTree(Integer headlineId, Integer page, Integer pageSize, Integer status) {
        // 1. 获取所有有效评论，按时间正序加载以构建正确层级（时间复杂度 O(N)）
        List<Comment> allComments = commentRepository.findByNewsIdAndIsDeletedOrderByCreatedAtAsc(headlineId, false);
        
        if (status != null) {
            allComments.removeIf(c -> !c.getStatus().equals(status));
        }

        if (allComments.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 空间换时间：建立 ID 索引，O(1) 瞬时寻址
        Map<String, Map<String, Object>> lookup = new HashMap<>();
        List<Map<String, Object>> allNodes = new ArrayList<>();
        
        for (Comment comment : allComments) {
            Map<String, Object> node = new LinkedHashMap<>(); // 使用 LinkedHashMap 保持属性顺序
            node.put("id", comment.getId());
            node.put("content", comment.getContent());
            node.put("userInfo", comment.getUserInfo());
            node.put("newsId", comment.getNewsId());
            node.put("parentId", comment.getParentId());
            node.put("likeCount", comment.getLikeCount());
            node.put("replyCount", comment.getReplyCount());
            node.put("status", comment.getStatus());
            node.put("createdAt", comment.getCreatedAt());
            node.put("updatedAt", comment.getUpdatedAt());
            node.put("children", new ArrayList<Map<String, Object>>());
            
            lookup.put(comment.getId(), node);
            allNodes.add(node);
        }

        // 3. 线性遍历组装树：由于 allComments 是正序，父节点必然在子节点之前被加载到 lookup
        List<Map<String, Object>> rootNodes = new ArrayList<>();
        for (Comment comment : allComments) {
            Map<String, Object> currentNode = lookup.get(comment.getId());
            String parentId = comment.getParentId();
            
            if (parentId == null || parentId.trim().isEmpty()) {
                rootNodes.add(currentNode);
            } else {
                Map<String, Object> parentNode = lookup.get(parentId);
                if (parentNode != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parentNode.get("children");
                    children.add(currentNode);
                } else {
                    rootNodes.add(currentNode);
                }
            }
        }

        // 4. 对顶级评论进行倒序排列（最新的在最上面），但保持子评论为正序（交流连贯性）
        Collections.reverse(rootNodes);

        // 5. 分页处理
        int totalRoots = rootNodes.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalRoots);
        
        if (startIndex >= totalRoots) {
            return new ArrayList<>();
        }
        
        return rootNodes.subList(startIndex, endIndex);
    }
}

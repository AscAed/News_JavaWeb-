package com.zhouyi.service;

import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.dto.CommentUpdateDTO;
import com.zhouyi.dto.CommentStatusDTO;
import com.zhouyi.dto.CommentLikeDTO;
import com.zhouyi.entity.mongodb.Comment;
import com.zhouyi.repository.mongodb.CommentRepository;
import com.zhouyi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评论服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private CommentServiceImpl commentService;
    
    private Comment testComment;
    private CommentCreateDTO commentCreateDTO;
    
    @BeforeEach
    void setUp() {
        // 创建测试评论
        testComment = new Comment();
        testComment.setId("test-comment-id");
        testComment.setNewsId(1001);
        testComment.setUserId(1);
        testComment.setContent("测试评论内容");
        testComment.setParentId(null);
        testComment.setLikeCount(5);
        testComment.setReplyCount(2);
        testComment.setIsDeleted(false);
        testComment.setStatus(1);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        
        // 创建测试DTO
        commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setHeadlineId(1001);
        commentCreateDTO.setContent("新的评论内容");
        commentCreateDTO.setParentId(null);
    }
    
    @Test
    void testGetCommentById_Success() {
        // Given
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        
        // When
        var result = commentService.getCommentById("test-comment-id");
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("test-comment-id", result.getData().getId());
        verify(commentRepository).findById("test-comment-id");
    }
    
    @Test
    void testGetCommentById_NotFound() {
        // Given
        when(commentRepository.findById("invalid-id")).thenReturn(Optional.empty());
        
        // When
        var result = commentService.getCommentById("invalid-id");
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("评论不存在", result.getMessage());
    }
    
    @Test
    void testCreateComment_Success() {
        // Given
        when(userService.getUserById(1)).thenReturn(com.zhouyi.common.result.Result.success(new com.zhouyi.entity.User()));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        var result = commentService.createComment(commentCreateDTO, 1);
        
        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testCreateComment_UserNotFound() {
        // Given
        when(userService.getUserById(999)).thenReturn(com.zhouyi.common.result.Result.error(404, "用户不存在"));
        
        // When
        var result = commentService.createComment(commentCreateDTO, 999);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        verify(userService).getUserById(999);
        verify(commentRepository, never()).save(any());
    }
    
    @Test
    void testUpdateComment_Success() {
        // Given
        CommentUpdateDTO updateDTO = new CommentUpdateDTO();
        updateDTO.setContent("更新后的评论内容");
        
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        var result = commentService.updateComment("test-comment-id", updateDTO, 1);
        
        // Then
        assertTrue(result.isSuccess());
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testUpdateComment_Unauthorized() {
        // Given
        CommentUpdateDTO updateDTO = new CommentUpdateDTO();
        updateDTO.setContent("更新后的评论内容");
        
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        
        // When
        var result = commentService.updateComment("test-comment-id", updateDTO, 999);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        verify(commentRepository, never()).save(any());
    }
    
    @Test
    void testDeleteComment_Success() {
        // Given
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        var result = commentService.deleteComment("test-comment-id", 1);
        
        // Then
        assertTrue(result.isSuccess());
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testLikeComment_Success() {
        // Given
        CommentLikeDTO likeDTO = new CommentLikeDTO();
        likeDTO.setAction("like");
        
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        var result = commentService.likeComment("test-comment-id", likeDTO, 1);
        
        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testUpdateCommentStatus_Success() {
        // Given
        CommentStatusDTO statusDTO = new CommentStatusDTO();
        statusDTO.setStatus(0);
        statusDTO.setReason("内容不当");
        
        when(commentRepository.findById("test-comment-id")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        var result = commentService.updateCommentStatus("test-comment-id", statusDTO, 1);
        
        // Then
        assertTrue(result.isSuccess());
        verify(commentRepository).save(any(Comment.class));
    }
}

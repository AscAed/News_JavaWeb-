package com.zhouyi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.service.CommentService;
import com.zhouyi.common.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 评论控制器测试类
 */
@WebMvcTest(CommentController.class)
@org.springframework.context.annotation.Import({
                com.zhouyi.config.SecurityConfig.class,
                com.zhouyi.common.filter.JwtAuthenticationFilter.class,
                com.zhouyi.common.security.CustomAuthenticationEntryPoint.class,
                com.zhouyi.common.security.CustomAccessDeniedHandler.class
})
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CommentService commentService;

        @MockBean
        private JwtUtil jwtUtil;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testGetCommentsByHeadline_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.getCommentsByHeadline(anyInt(), anyInt(), anyInt(), anyString(), anyString(),
                                any()))
                                .thenReturn(Result.success(Map.of("total", 10, "items", new ArrayList<>())));

                // When & Then
                mockMvc.perform(get("/api/v1/headlines/1001/comments")
                                .param("page", "1")
                                .param("page_size", "10")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testGetCommentsByHeadline_InvalidHeadlineId() throws Exception {
                // Given
                when(commentService.getCommentsByHeadline(eq(0), anyInt(), anyInt(), anyString(), anyString(), any()))
                                .thenReturn(Result.error(400, "新闻ID不能为空且必须为正数"));

                // When & Then
                mockMvc.perform(get("/api/v1/headlines/0/comments")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        void testCreateComment_Success() throws Exception {
                // Given
                CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
                commentCreateDTO.setHeadlineId(1001);
                commentCreateDTO.setContent("测试评论内容");
                commentCreateDTO.setParentId(null);

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.createComment(any(CommentCreateDTO.class), anyInt()))
                                .thenReturn(Result.success(new com.zhouyi.entity.mongodb.Comment()));

                // When & Then
                mockMvc.perform(post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentCreateDTO))
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testCreateComment_Unauthorized() throws Exception {
                // Given
                CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
                commentCreateDTO.setHeadlineId(1001);
                commentCreateDTO.setContent("测试评论内容");

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn(null);

                // When & Then
                mockMvc.perform(post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentCreateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        void testUpdateComment_Success() throws Exception {
                // Given
                Map<String, Object> updateData = Map.of("content", "更新后的评论内容");

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.updateComment(anyString(), any(), anyInt()))
                                .thenReturn(Result.success(new com.zhouyi.entity.mongodb.Comment()));

                // When & Then
                mockMvc.perform(put("/api/v1/comments/test-comment-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData))
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testDeleteComment_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.deleteComment(anyString(), anyInt()))
                                .thenReturn(Result.success());

                // When & Then
                mockMvc.perform(delete("/api/v1/comments/test-comment-id")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testLikeComment_Success() throws Exception {
                // Given
                Map<String, Object> likeData = Map.of("action", "like");

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.likeComment(anyString(), any(), anyInt()))
                                .thenReturn(Result.success(Map.of("like_count", 6, "is_liked", true)));

                // When & Then
                mockMvc.perform(post("/api/v1/comments/test-comment-id/like")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(likeData))
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testGetCommentsByUser_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(commentService.getCommentsByUser(anyInt(), anyInt(), anyInt(), any()))
                                .thenReturn(Result.success(Map.of("total", 5, "items", new ArrayList<>())));

                // When & Then
                mockMvc.perform(get("/api/v1/users/1/comments")
                                .param("page", "1")
                                .param("page_size", "10")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }
}

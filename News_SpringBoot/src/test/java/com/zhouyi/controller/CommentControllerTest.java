package com.zhouyi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.common.result.Result;
import com.zhouyi.dto.CommentCreateDTO;
import com.zhouyi.service.CommentService;
import com.zhouyi.service.UserService;
import com.zhouyi.common.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 评论控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_comment;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "mybatis.mapper-locations=classpath:mappers/*xml",
        "mybatis.type-aliases-package=com.zhouyi.model",
        "mybatis.configuration.map-underscore-to-camel-case=true",
        "spring.profiles.active=test"
})
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

    @MockitoBean
        private CommentService commentService;

    @MockitoBean
        private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private com.zhouyi.service.UserRoleService userRoleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @org.junit.jupiter.api.BeforeEach
    void setUpMocks() {
        com.zhouyi.entity.User mockUser = new com.zhouyi.entity.User();
        mockUser.setId(1);
        mockUser.setPhone("13800138000");
        mockUser.setStatus(1);

        when(userService.getUserByPhone("13800138000"))
                .thenReturn(Result.successWithDataAndPath(mockUser, null));

        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(Result.successWithDataAndPath(java.util.List.of(), null));
    }

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
                        .andDo(print())
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

package com.zhouyi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.common.result.Result;
import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.service.FavoriteServiceExtended;
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
 * 收藏控制器测试类
 */
@WebMvcTest(FavoriteController.class)
@org.springframework.context.annotation.Import({
                com.zhouyi.config.SecurityConfig.class,
                com.zhouyi.common.filter.JwtAuthenticationFilter.class,
                com.zhouyi.common.security.CustomAuthenticationEntryPoint.class,
                com.zhouyi.common.security.CustomAccessDeniedHandler.class
})
public class FavoriteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private FavoriteServiceExtended favoriteService;

        @MockBean
        private JwtUtil jwtUtil;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testGetFavorites_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(favoriteService.getFavoritesByUser(anyInt(), anyInt(), anyInt(), any(), anyString(), anyString()))
                                .thenReturn(Result.success(Map.of("total", 10, "items", new ArrayList<>())));

                // When & Then
                mockMvc.perform(get("/api/v1/favorites")
                                .param("page", "1")
                                .param("page_size", "10")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testGetFavorites_Unauthorized() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/api/v1/favorites"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        void testAddFavorite_Success() throws Exception {
                // Given
                FavoriteCreateDTO favoriteCreateDTO = new FavoriteCreateDTO();
                favoriteCreateDTO.setHeadlineId(1001);
                favoriteCreateDTO.setNote("测试收藏备注");

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(favoriteService.addFavorite(any(FavoriteCreateDTO.class), anyInt()))
                                .thenReturn(Result.success(new com.zhouyi.entity.Favorite()));

                // When & Then
                mockMvc.perform(post("/api/v1/favorites")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(favoriteCreateDTO))
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testAddFavorite_Unauthorized() throws Exception {
                // Given
                FavoriteCreateDTO favoriteCreateDTO = new FavoriteCreateDTO();
                favoriteCreateDTO.setHeadlineId(1001);

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn(null);

                // When & Then
                mockMvc.perform(post("/api/v1/favorites")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(favoriteCreateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        void testRemoveFavorite_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(favoriteService.removeFavorite(anyInt(), anyInt()))
                                .thenReturn(Result.success());

                // When & Then
                mockMvc.perform(delete("/api/v1/favorites/1")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testUpdateFavoriteNote_Success() throws Exception {
                // Given
                Map<String, Object> updateData = Map.of("note", "更新后的收藏备注");

                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(favoriteService.updateFavoriteNote(anyInt(), any(), anyInt()))
                                .thenReturn(Result.success());

                // When & Then
                mockMvc.perform(patch("/api/v1/favorites/1/note")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData))
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testGetFavoriteCountByHeadline_Success() throws Exception {
                // Given
                when(favoriteService.getFavoriteCountByNews(anyInt()))
                                .thenReturn(Result.success(Map.of("headline_id", 1001, "favorite_count", 15)));

                // When & Then
                mockMvc.perform(get("/api/v1/headlines/1001/favorites/count")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.favorite_count").value(15));
        }

        @Test
        void testCheckFavoriteStatus_Success() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken("valid-token")).thenReturn(true);
                when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);

                when(favoriteService.checkFavoriteStatus(anyInt(), anyInt()))
                                .thenReturn(Result.success(Map.of("headline_id", 1001, "is_favorited", true)));

                // When & Then
                mockMvc.perform(get("/api/v1/headlines/1001/favorites/status")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.is_favorited").value(true));
        }

        @Test
        void testCheckFavoriteStatus_Unauthorized() throws Exception {
                // Given
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/api/v1/headlines/1001/favorites/status"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }
}

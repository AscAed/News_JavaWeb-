package com.zhouyi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.zhouyi.common.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 分类管理和收藏批量操作集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:catfavtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "mybatis.mapper-locations=classpath:mappers/*xml",
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.profiles.active=test"
})
public class CategoryAndFavoriteIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private JwtUtil jwtUtil;

        @BeforeEach
        void setUp() {
                when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.getPhoneFromToken(anyString())).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1);
        }

        @Test
        public void testNewsTypeManagement() throws Exception {
                // 测试创建分类
                mockMvc.perform(post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"测试分类\",\"description\":\"这是一个测试分类\",\"sortOrder\":1,\"color\":\"#FF0000\"}")
                                .header("Authorization", "Bearer valid-token"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));

                // 测试获取分类列表
                mockMvc.perform(get("/api/v1/categories")
                                .param("status", "1")
                                .param("sort_by", "sort_order")
                                .param("sort_order", "asc")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));

                // 测试更新分类状态
                mockMvc.perform(patch("/api/v1/categories/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":0,\"reason\":\"测试禁用\"}"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testFavoriteBatchOperations() throws Exception {
                // 测试批量添加收藏
                mockMvc.perform(post("/api/v1/favorites/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"action\":\"add\",\"headlineIds\":[1001,1002,1003],\"note\":\"批量添加测试\"}")
                                .header("Authorization", "Bearer valid-token"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));

                // 测试批量删除收藏
                mockMvc.perform(post("/api/v1/favorites/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"action\":\"remove\",\"headlineIds\":[1001,1002,1003]}")
                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }
}

package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.service.RssService;
import com.zhouyi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.ArrayList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * RSS控制器权限测试 - 验证ADMIN权限修复
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_rss;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "mybatis.mapper-locations=classpath:mappers/*xml",
        "mybatis.type-aliases-package=com.zhouyi.model",
        "mybatis.configuration.map-underscore-to-camel-case=true",
        "spring.profiles.active=test"
})
class RssSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RssSubscriptionController rssSubscriptionController;

    @MockitoBean
    private RssService rssService;

    @MockitoBean
    private com.zhouyi.common.utils.JwtUtil jwtUtil;

    @MockitoBean
    private com.zhouyi.service.UserService userService;

    @MockitoBean
    private com.zhouyi.service.UserRoleService userRoleService;

    @org.junit.jupiter.api.BeforeEach
    void setUpMocks() {
        com.zhouyi.entity.User mockUser = new com.zhouyi.entity.User();
        mockUser.setId(1);
        mockUser.setPhone("13800138000");
        mockUser.setStatus(1);

        when(userService.getUserByPhone("13800138000"))
                .thenReturn(Result.successWithDataAndPath(mockUser, null));

        // Default: no roles
        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(Result.successWithDataAndPath(java.util.List.of(), null));

        // JWT Mocks
        when(jwtUtil.extractTokenFromRequest(any())).thenReturn("valid-token");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.getPhoneFromToken("valid-token")).thenReturn("13800138000");
        when(jwtUtil.getUserIdFromToken("valid-token")).thenReturn(1);
    }

    @Test
    void testFetchRssContent_WithAdminRole_ShouldSucceed() throws Exception {
        // Given: Set Admin role
        var adminRole = new com.zhouyi.entity.Role();
        adminRole.setId(1);
        adminRole.setRoleName("ADMIN");
        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(Result.successWithDataAndPath(java.util.List.of(adminRole), null));

        // 模拟RSS服务返回成功结果
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("subscription_id", 1L);
        mockResult.put("fetched_count", 25);
        mockResult.put("new_articles", 20);
        mockResult.put("fetch_time", "2025-11-25T12:05:00Z");

        when(rssService.fetchAndSave(any(Long.class))).thenReturn(mockResult);

        // 执行请求
        mockMvc.perform(post("/api/v1/rss-subscriptions/1/fetch")
                        .header("Authorization", "Bearer valid-token")
                        .with(csrf())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.subscription_id").value(1))
                .andExpect(jsonPath("$.data.fetched_count").value(25));
    }

    @Test
    void testFetchRssContent_WithUserRole_ShouldReturn403() throws Exception {
        // Given: Set User role
        var userRole = new com.zhouyi.entity.Role();
        userRole.setId(2);
        userRole.setRoleName("USER");
        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(Result.successWithDataAndPath(java.util.List.of(userRole), null));

        // 执行请求
        mockMvc.perform(post("/api/v1/rss-subscriptions/1/fetch")
                        .header("Authorization", "Bearer valid-token")
                        .with(csrf())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()) // Custom behavior: returns 200
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void testFetchRssContent_WithoutAuthentication_ShouldReturn401() throws Exception {
        // Given: No token
        when(jwtUtil.extractTokenFromRequest(any())).thenReturn(null);

        // 执行请求
        mockMvc.perform(post("/api/v1/rss-subscriptions/1/fetch")
                        .with(csrf())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()) // Custom behavior: returns 200
                .andExpect(jsonPath("$.code").value(401));
    }
}

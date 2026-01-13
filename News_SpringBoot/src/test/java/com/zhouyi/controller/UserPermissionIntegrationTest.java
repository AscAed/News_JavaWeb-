package com.zhouyi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户权限集成测试 - 验证登录时返回正确的角色信息
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:permissiontestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "mybatis.mapper-locations=classpath:mappers/*xml",
        "mybatis.configuration.map-underscore-to-camel-case=true",
        "spring.sql.init.mode=always"
})
class UserPermissionIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    void testAdminLoginReturnsCorrectRole() throws Exception {
        // 使用 data.sql 中的管理员用户: phone=13800138000, password=Password123
        String loginRequest = "{\"phone\":\"13800138000\", \"password\":\"Password123\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.role_id").value(1))
                .andExpect(jsonPath("$.data.user.role_name").value("admin"));
    }

    @Test
    void testUserLoginReturnsCorrectRole() throws Exception {
        // 使用 data.sql 中的普通用户: phone=13800138001, password=Password123
        String loginRequest = "{\"phone\":\"13800138001\", \"password\":\"Password123\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.id").value(2))
                .andExpect(jsonPath("$.data.user.role_id").value(2))
                .andExpect(jsonPath("$.data.user.role_name").value("user"));
    }
}

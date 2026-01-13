package com.zhouyi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 统计模块集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:statstestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "mybatis.mapper-locations=classpath:mappers/*xml",
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.profiles.active=test"
})
public class StatisticsIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        /**
         * 测试获取新闻统计数据
         */
        @Test
        @WithMockUser(roles = { "ADMIN" })
        public void testGetNewsStatistics() throws Exception {
                mockMvc.perform(get("/api/v1/statistics/news")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.message").value("操作成功"))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.totalNews").exists())
                                .andExpect(jsonPath("$.data.todayNews").exists())
                                .andExpect(jsonPath("$.data.totalViews").exists())
                                .andExpect(jsonPath("$.data.topCategories").exists());
        }

        /**
         * 测试获取用户统计数据
         */
        @Test
        @WithMockUser(roles = { "ADMIN" })
        public void testGetUserStatistics() throws Exception {
                mockMvc.perform(get("/api/v1/statistics/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.message").value("操作成功"))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.totalUsers").exists())
                                .andExpect(jsonPath("$.data.todayUsers").exists())
                                .andExpect(jsonPath("$.data.activeUsers").exists());
        }

        /**
         * 测试非管理员用户访问新闻统计接口
         */
        @Test
        @WithMockUser(roles = { "USER" })
        public void testGetNewsStatisticsWithoutAdminRole() throws Exception {
                mockMvc.perform(get("/api/v1/statistics/news")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(403));
        }

        /**
         * 测试非管理员用户访问用户统计接口
         */
        @Test
        @WithMockUser(roles = { "USER" })
        public void testGetUserStatisticsWithoutAdminRole() throws Exception {
                mockMvc.perform(get("/api/v1/statistics/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(403));
        }

        /**
         * 测试未认证用户访问统计接口
         */
        @Test
        public void testGetStatisticsWithoutAuthentication() throws Exception {
                mockMvc.perform(get("/api/v1/statistics/news")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));

                mockMvc.perform(get("/api/v1/statistics/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }
}

package com.zhouyi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户登录集成测试
 */
@SpringBootTest
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:logintestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "mybatis.mapper-locations=classpath:mappers/*xml",
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.sql.init.mode=always",
                "spring.sql.init.data-locations=classpath:empty.sql"
})
@org.springframework.transaction.annotation.Transactional
class UserLoginIntegrationTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Test
        void testLoginParameterValidation() throws Exception {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

                // 测试空手机号
                String invalidPhoneRequest = "{\"phone\":\"\", \"password\":\"Password123\"}";
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidPhoneRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").exists());

                // 测试无效手机号格式
                String invalidPhoneFormatRequest = "{\"phone\":\"123\", \"password\":\"Password123\"}";
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidPhoneFormatRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value(org.hamcrest.Matchers.containsString("手机号格式错误")));

                // 测试空密码
                String emptyPasswordRequest = "{\"phone\":\"13800138000\", \"password\":\"\"}";
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(emptyPasswordRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").exists());

                // 测试无效密码格式
                String invalidPasswordRequest = "{\"phone\":\"13800138000\", \"password\":\"123\"}";
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidPasswordRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
        }

        @Autowired
        private com.zhouyi.mapper.UserMapper userMapper;

        @Test
        void testLoginSuccess() throws Exception {
                // 准备测试用户
                com.zhouyi.entity.User user = new com.zhouyi.entity.User();
                user.setPhone("13912345678");
                user.setUsername("LoginTestUser");
                user.setPassword(com.zhouyi.common.utils.PasswordUtil.encodePassword("Password123"));
                user.setStatus(1);
                userMapper.insertUser(user);

                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

                String loginRequest = "{\"phone\":\"13912345678\", \"password\":\"Password123\"}";
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.token").exists())
                                .andExpect(jsonPath("$.data.user.username").value("LoginTestUser"));
        }
}

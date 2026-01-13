package com.zhouyi.controller;

import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "mybatis.mapper-locations=classpath:mappers/*xml",
                "mybatis.type-aliases-package=com.zhouyi.model",
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.profiles.active=test"
})
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

    @MockitoBean
        private JwtUtil jwtUtil;

    @MockitoBean
        private UserService userService;

        @Test
        void testValidateTokenSuccess() throws Exception {
                String validToken = "valid.jwt.token";
                when(jwtUtil.validateToken(validToken)).thenReturn(true);

                mockMvc.perform(post("/api/v1/auth/validate")
                                .param("token", validToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        void testValidateTokenInvalid() throws Exception {
                String invalidToken = "invalid.jwt.token";
                when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

                mockMvc.perform(post("/api/v1/auth/validate")
                                .param("token", invalidToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        void testValidateTokenException() throws Exception {
                String token = "exception.token";
                when(jwtUtil.validateToken(token)).thenThrow(new RuntimeException("Token error"));

                mockMvc.perform(post("/api/v1/auth/validate")
                                .param("token", token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        void testGetCurrentUserSuccess() throws Exception {
                User mockUser = new User();
                mockUser.setId(1);
                mockUser.setPhone("13800138000");
                mockUser.setUsername("测试用户");
                mockUser.setEmail("test@example.com");
                mockUser.setStatus(1);

                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.getPhoneFromToken(anyString())).thenReturn("13800138000");
                when(userService.getUserByPhone(anyString()))
                        .thenReturn(com.zhouyi.common.result.Result.successWithMessageAndData("查询成功",
                                mockUser));

                mockMvc.perform(get("/api/v1/auth/me")
                                .header("Authorization", "Bearer valid.jwt.token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                                .andExpect(jsonPath("$.data.username").value("测试用户"));
        }

        @Test
        void testGetCurrentUserNotFound() throws Exception {
                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.getPhoneFromToken(anyString())).thenReturn("13800138000");
                when(userService.getUserByPhone(anyString()))
                                .thenReturn(com.zhouyi.common.result.Result.error("用户不存在"));

                mockMvc.perform(get("/api/v1/auth/me")
                                .header("Authorization", "Bearer valid.jwt.token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("用户不存在"));
        }

        @Test
        void testLogoutSuccess() throws Exception {
                mockMvc.perform(post("/api/v1/auth/logout"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.message").value("登出成功"));
        }

        @Test
        void testRefreshTokenSuccess() throws Exception {
                User mockUser = new User();
                mockUser.setId(1);
                mockUser.setPhone("13800138000");

                when(userService.getUserByPhone(anyString()))
                        .thenReturn(com.zhouyi.common.result.Result.successWithMessageAndData("查询成功",
                                mockUser));
                when(jwtUtil.getPhoneFromToken(anyString()))
                                .thenReturn("13800138000");
                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.validateTokenType(anyString(), anyString())).thenReturn(true);
                when(jwtUtil.generateToken(1, "13800138000"))
                                .thenReturn("new.jwt.token");

                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"old.jwt.token\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.user.phone").value("13800138000"));
        }

        @Test
        void testRefreshTokenUserNotFound() throws Exception {
                when(jwtUtil.getPhoneFromToken(anyString()))
                                .thenReturn("13800138000");
                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.validateTokenType(anyString(), anyString())).thenReturn(true);
                when(userService.getUserByPhone(anyString()))
                                .thenReturn(com.zhouyi.common.result.Result.error("用户不存在"));

                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"old.jwt.token\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("用户不存在"));
        }
}

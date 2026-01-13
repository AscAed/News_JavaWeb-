package com.zhouyi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.UserLoginDTO;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试类 - 测试登录参数校验
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
class UserControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private ObjectMapper objectMapper;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoginSuccess() throws Exception {
        // 模拟成功的登录
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setPhone("13800138000");
        mockUser.setUsername("测试用户");
        mockUser.setEmail("test@example.com");
        mockUser.setStatus(1);

        when(userService.login(anyString(), anyString()))
                .thenReturn(Result.successWithMessageAndData("登录成功", mockUser));

        // Mock JWT token generation
        when(jwtUtil.generateToken(anyInt(), anyString()))
                .thenReturn("mock.jwt.token");

        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"));
    }

    @Test
    void testLoginWithEmptyPhone() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("", "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testLoginWithInvalidPhoneFormat() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("123", "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("手机号格式错误")));
    }

    @Test
    void testLoginWithEmptyPassword() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testLoginWithInvalidPasswordFormat() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }

    @Test
    void testLoginWithPasswordMissingUppercase() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }

    @Test
    void testLoginWithPasswordMissingLowercase() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "PASSWORD123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }

    @Test
    void testLoginWithPasswordMissingNumber() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "Password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }

    @Test
    void testLoginWithPasswordTooShort() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "Pa1");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }

    @Test
    void testLoginWithPasswordTooLong() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("13800138000", "Password123Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("密码格式错误")));
    }
}

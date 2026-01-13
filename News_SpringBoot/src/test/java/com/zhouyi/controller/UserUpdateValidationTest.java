package com.zhouyi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.dto.UserUpdateDTO;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.zhouyi.common.result.Result;

/**
 * 用户更新接口参数校验测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "jwt.secret=bXlfc2VjdXJlX2p3dF9zZWNyZXRfa2V5XzIwMjVfZm9yX25ld3Nfc3ByaW5nYm9vdF9hcHBsaWNhdGlvbl9tdXN0X2JlX2F0X2xlYXN0XzY0X2J5dGVzX2xvbmdfZW5vdWdoX2Zvcl9obWFjX3NoYV81MTI=",
        "jwt.expiration=3600000"
})
public class UserUpdateValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * 生成有效的JWT token用于测试
     */
    private String generateTestToken() {
        // 模拟一个有效的JWT token
        return "Bearer valid.test.token.for.testing";
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * 设置JWT认证模拟（供所有测试使用）
     */
    private void setupJwtAuthentication() {
        // 模拟JWT token验证
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getPhoneFromToken(anyString())).thenReturn("13800138000");

        // 模拟用户存在
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setPhone("13800138000");
        when(userService.getUserByPhone(anyString())).thenReturn(Result.successWithMessageAndData("用户存在", mockUser));
    }

    /**
     * 测试正常更新用户
     */
    @Test
    public void testUpdateUserSuccess() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();
        when(userService.updateUser(any())).thenReturn(Result.successWithMessageAndData("更新用户成功", "SUCCESS"));

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("13800138000");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试用户ID为空
     */
    @Test
    public void testUpdateUserIdNull() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(null);
        userUpdateDTO.setPhone("13800138000");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试手机号为空
     */
    @Test
    public void testUpdateUserPhoneEmpty() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试手机号格式错误
     */
    @Test
    public void testUpdateUserPhoneInvalid() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("123456789");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试用户名为空
     */
    @Test
    public void testUpdateUserUsernameEmpty() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("13800138000");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试邮箱格式错误
     */
    @Test
    public void testUpdateUserEmailInvalid() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("13800138000");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("invalid-email");
        userUpdateDTO.setStatus(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试状态为空
     */
    @Test
    public void testUpdateUserStatusNull() throws Exception {
        // 设置JWT认证
        setupJwtAuthentication();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1);
        userUpdateDTO.setPhone("13800138000");
        userUpdateDTO.setPassword("newpassword123");
        userUpdateDTO.setUsername("更新用户");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setStatus(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/test/update-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", generateTestToken())
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }
}

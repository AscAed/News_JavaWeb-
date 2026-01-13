package com.zhouyi.controller;

import com.zhouyi.dto.UserRegistDTO;
import com.zhouyi.entity.User;
import com.zhouyi.mapper.UserMapper;
import com.zhouyi.common.utils.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户注册集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Transactional
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:regtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "mybatis.mapper-locations=classpath:mappers/*xml",
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.sql.init.mode=always",
                "spring.sql.init.data-locations=classpath:empty.sql"
})
class UserRegistrationIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserMapper userMapper;

    @Mock
        private ObjectMapper objectMapper;

        @Test
        void testRegistrationSuccessAndHashing() throws Exception {
                UserRegistDTO registDTO = new UserRegistDTO();
                registDTO.setUsername("NewUser_123");
                registDTO.setPhone("13912345678");
                registDTO.setPassword("Password123");
                registDTO.setEmail("newuser@example.com");

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.code").value(201))
                                .andExpect(jsonPath("$.data.username").value("NewUser_123"))
                                .andExpect(jsonPath("$.data.phone").value("13912345678"))
                                .andExpect(jsonPath("$.data.user_id").exists());

                // 验证数据库中是否存在该用户且密码已加密
                User user = userMapper.selectUserByPhone("13912345678");
                assertNotNull(user);
                assertNotEquals("Password123", user.getPassword());
                assertTrue(PasswordUtil.matches("Password123", user.getPassword()));
        }

        @Test
        void testRegistrationValidationFailure() throws Exception {
                UserRegistDTO registDTO = new UserRegistDTO();
                registDTO.setUsername("a"); // 太短
                registDTO.setPhone("123"); // 格式错误
                registDTO.setPassword("123"); // 格式错误
                registDTO.setEmail("invalid-email"); // 格式错误

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        void testRegistrationDuplicatePhoneFailure() throws Exception {
                // 1. 先注册一个用户
                UserRegistDTO registDTO = new UserRegistDTO();
                registDTO.setUsername("FirstUser");
                registDTO.setPhone("13811112222");
                registDTO.setPassword("Password123");
                registDTO.setEmail("first@example.com");

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registDTO)))
                                .andExpect(status().isCreated());

                // 2. 尝试使用相同的手机号再次注册
                UserRegistDTO duplicateDTO = new UserRegistDTO();
                duplicateDTO.setUsername("SecondUser");
                duplicateDTO.setPhone("13811112222"); // 重复
                duplicateDTO.setPassword("Password456");
                duplicateDTO.setEmail("second@example.com");

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(duplicateDTO)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.code").value(409))
                                .andExpect(jsonPath("$.message").exists())
                                .andExpect(jsonPath("$.success").value(false));
        }
}

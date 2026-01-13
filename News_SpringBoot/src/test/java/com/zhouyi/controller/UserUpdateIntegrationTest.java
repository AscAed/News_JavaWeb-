package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import com.zhouyi.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User update endpoints integration tests
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
                "mybatis.configuration.map-underscore-to-camel-case=true",
                "spring.profiles.active=test"
})
class UserUpdateIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserService userService;

        @MockBean
        private VerificationService verificationService;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
                when(jwtUtil.validateToken(anyString())).thenReturn(true);
                when(jwtUtil.getPhoneFromToken(anyString())).thenReturn("13800138000");
                when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1);
        }

        @Test
        void testUpdateUserProfile_Success() throws Exception {
                when(userService.updateUserProfileByUser(eq(1), anyString(), anyString()))
                                .thenReturn(Result.success("Profile updated successfully"));

                mockMvc.perform(put("/api/v1/users/1/profile")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"NewUsername\",\"avatar\":\"https://example.com/avatar.jpg\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
        }

        @Test
        void testUpdateUserProfile_Unauthorized() throws Exception {
                when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(2);

                mockMvc.perform(put("/api/v1/users/1/profile")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"NewUsername\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("无权修改其他用户的个人资料"));
        }

        @Test
        void testUpdateUserProfile_NoToken() throws Exception {
                // Clear SecurityContext by making jwtUtil return false or null
                when(jwtUtil.validateToken(anyString())).thenReturn(false);

                mockMvc.perform(put("/api/v1/users/1/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"NewUsername\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateUserAttributes_Success() throws Exception {
                when(userService.updateUserAttributesByAdmin(eq(1), eq(0), isNull()))
                                .thenReturn(Result.success("User attributes updated successfully"));

                mockMvc.perform(put("/api/v1/users/1/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":0}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testRequestEmailUpdate_Success() throws Exception {
                when(userService.requestEmailUpdate(eq(1), anyString()))
                                .thenReturn(Result.success(
                                                "Email update request initiated. Verification code will be sent."));

                mockMvc.perform(post("/api/v1/users/1/email/request")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"newEmail\":\"newemail@example.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(
                                                jsonPath("$.message").value(
                                                                "Email update request initiated. Verification code will be sent."));
        }

        @Test
        void testRequestEmailUpdate_Unauthorized() throws Exception {
                when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(2);

                mockMvc.perform(post("/api/v1/users/1/email/request")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"newEmail\":\"newemail@example.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("无权修改其他用户的邮箱"));
        }

        @Test
        void testVerifyEmailUpdate_Success() throws Exception {
                when(userService.verifyAndUpdateEmail(eq(1), eq("123456")))
                                .thenReturn(Result.success("Email verification successful. Email will be updated."));

                mockMvc.perform(post("/api/v1/users/1/email/verify")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"123456\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testVerifyEmailUpdate_InvalidToken() throws Exception {
                when(userService.verifyAndUpdateEmail(eq(1), eq("999999")))
                                .thenReturn(Result.error("Invalid verification code"));

                mockMvc.perform(post("/api/v1/users/1/email/verify")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"999999\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("Invalid verification code"));
        }

        @Test
        void testRequestPhoneUpdate_Success() throws Exception {
                when(userService.requestPhoneUpdate(eq(1), anyString()))
                                .thenReturn(Result.success(
                                                "Phone update request initiated. Verification code will be sent."));

                mockMvc.perform(post("/api/v1/users/1/phone/request")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"newPhone\":\"13900139000\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testVerifyPhoneUpdate_Success() throws Exception {
                when(userService.verifyAndUpdatePhone(eq(1), eq("654321")))
                                .thenReturn(Result.success("Phone verification successful. Phone will be updated."));

                mockMvc.perform(post("/api/v1/users/1/phone/verify")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"654321\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void testVerifyPhoneUpdate_Unauthorized() throws Exception {
                when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(2);

                mockMvc.perform(post("/api/v1/users/1/phone/verify")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"654321\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("无权修改其他用户的手机号"));
        }
}

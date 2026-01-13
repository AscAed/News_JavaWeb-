package com.zhouyi.common.utils;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=bXlfc2VjdXJlX2p3dF9zZWNyZXRfa2V5XzIwMjVfZm9yX25ld3Nfc3ByaW5nYm9vdF9hcHBsaWNhdGlvbl9tdXN0X2JlX2F0X2xlYXN0XzY0X2J5dGVzX2xvbmdfZW5vdWdoX2Zvcl9obWFjX3NoYV81MTI=",
    "jwt.expiration=3600000" // 1小时
})
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 手动设置配置值（因为在测试中@Value可能不生效）
        jwtUtil.setSecret("bXlfc2VjdXJlX2p3dF9zZWNyZXRfa2V5XzIwMjVfZm9yX25ld3Nfc3ByaW5nYm9vdF9hcHBsaWNhdGlvbl9tdXN0X2JlX2F0X2xlYXN0XzY0X2J5dGVzX2xvbmdfZW5vdWdoX2Zvcl9obWFjX3NoYV81MTI=");
        jwtUtil.setExpiration(3600000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(1, "13800138000");
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT应该包含两个点
    }

    @Test
    void testGetPhoneFromToken() {
        String phone = "13800138000";
        String token = jwtUtil.generateToken(1, phone);
        
        String extractedPhone = jwtUtil.getPhoneFromToken(token);
        assertEquals(phone, extractedPhone);
    }

    @Test
    void testGetUserIdFromToken() {
        Integer userId = 123;
        String token = jwtUtil.generateToken(userId, "13800138000");
        
        Integer extractedUserId = jwtUtil.getUserIdFromToken(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testValidateToken() {
        String phone = "13800138000";
        String token = jwtUtil.generateToken(1, phone);
        
        assertTrue(jwtUtil.validateToken(token, phone));
        assertFalse(jwtUtil.validateToken(token, "13900139000")); // 错误的手机号
    }

    @Test
    void testValidateTokenWithoutUser() {
        String token = jwtUtil.generateToken(1, "13800138000");
        
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken(1, "13800138000");
        
        assertFalse(jwtUtil.isTokenExpired(token)); // 新生成的token不应该过期
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.jwt.token";
        
        assertTrue(jwtUtil.isTokenExpired(invalidToken)); // 无效token应该被认为是过期的
        assertFalse(jwtUtil.validateToken(invalidToken));
        assertFalse(jwtUtil.validateToken(invalidToken, "13800138000"));
    }

    @Test
    void testGetExpirationDateFromToken() {
        String token = jwtUtil.generateToken(1, "13800138000");
        
        java.util.Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
        assertNotNull(expirationDate);
        
        java.util.Date now = new java.util.Date();
        assertTrue(expirationDate.after(now)); // 过期时间应该在当前时间之后
        
        // 检查过期时间是否在预期范围内（1小时内）
        long timeDiff = expirationDate.getTime() - now.getTime();
        assertTrue(timeDiff > 3500000); // 应该接近1小时（3600000毫秒）
        assertTrue(timeDiff < 3700000); // 允许一些误差
    }

    @Test
    void testTokenWithDifferentUsers() {
        String token1 = jwtUtil.generateToken(1, "13800138000");
        String token2 = jwtUtil.generateToken(2, "13900139000");
        
        assertNotEquals(token1, token2); // 不同用户的token应该不同
        
        assertEquals("13800138000", jwtUtil.getPhoneFromToken(token1));
        assertEquals("13900139000", jwtUtil.getPhoneFromToken(token2));
        
        assertEquals(Integer.valueOf(1), jwtUtil.getUserIdFromToken(token1));
        assertEquals(Integer.valueOf(2), jwtUtil.getUserIdFromToken(token2));
    }

    @Test
    void testTokenTampering() {
        String originalToken = jwtUtil.generateToken(1, "13800138000");
        
        // 尝试篡改token（去掉最后一个字符）
        String tamperedToken = originalToken.substring(0, originalToken.length() - 1);
        
        assertThrows(JwtException.class, () -> {
            jwtUtil.getPhoneFromToken(tamperedToken);
        });
        
        assertTrue(jwtUtil.isTokenExpired(tamperedToken));
        assertFalse(jwtUtil.validateToken(tamperedToken));
    }
}

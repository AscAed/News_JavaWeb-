package com.zhouyi.common.utils;

import com.zhouyi.config.CustomProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类，用于生成和验证JWT token
 */
@Component
public class JwtUtil {

    @Autowired
    private CustomProperties customProperties;

    @Value("${jwt.secret:bXlfc2VjdXJlX2p3dF9zZWNyZXRfa2V5XzIwMjVfZm9yX25ld3Nfc3ByaW5nYm9vdF9hcHBsaWNhdGlvbl9tdXN0X2JlX2F0X2xlYXN0XzY0X2J5dGVzX2xvbmdfZW5vdWdoX2Zvcl9obWFjX3NoYV81MTI=}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 默认7天
    private Long refreshExpiration;

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param phone  用户手机号
     * @return JWT token
     */
    public String generateToken(Integer userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        claims.put("type", "access");

        return createToken(claims, phone, expiration);
    }

    /**
     * 生成JWT refresh token
     *
     * @param userId 用户ID
     * @param phone  用户手机号
     * @return JWT refresh token
     */
    public String generateRefreshToken(Integer userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        claims.put("type", "refresh");

        return createToken(claims, phone, refreshExpiration);
    }

    /**
     * 从token中获取用户手机号
     *
     * @param token JWT token
     * @return 用户手机号
     */
    public String getPhoneFromToken(String token) {
        return getClaimFromToken(token, "phone", String.class);
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        return getClaimFromToken(token, "userId", Integer.class);
    }

    /**
     * 从token中获取过期时间
     *
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定声明
     *
     * @param token          JWT token
     * @param claimsResolver 声明解析函数
     * @param <T>            返回类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.resolve(claims);
    }

    /**
     * 从token中获取指定声明
     *
     * @param token JWT token
     * @param claim 声明名称
     * @param clazz 类型
     * @param <T>   返回类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, String claim, Class<T> clazz) {
        final Claims claims = getAllClaimsFromToken(token);
        Object value = claims.get(claim);
        if (value == null) {
            return null;
        }

        if (clazz == Integer.class && value instanceof Number) {
            return clazz.cast(((Number) value).intValue());
        }

        return clazz.cast(value);
    }

    /**
     * 从token中获取所有声明
     *
     * @param token JWT token
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查token是否过期
     *
     * @param token JWT token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; // 解析失败则认为token无效
        }
    }

    /**
     * 生成token
     *
     * @param claims     声明
     * @param subject    主题（用户手机号）
     * @param expiration 过期时间（毫秒）
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证token类型
     *
     * @param token JWT token
     * @param type  期望的token类型（access/refresh）
     * @return 是否匹配
     */
    public Boolean validateTokenType(String token, String type) {
        try {
            String tokenType = getClaimFromToken(token, "type", String.class);
            return type.equals(tokenType);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 验证token
     *
     * @param token JWT token
     * @param phone 用户手机号
     * @return 是否有效
     */
    public Boolean validateToken(String token, String phone) {
        try {
            final String tokenPhone = getPhoneFromToken(token);
            return (tokenPhone.equals(phone) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 验证token是否有效（不检查用户）
     *
     * @param token JWT token
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        // 优先使用CustomProperties中的配置，如果没有则使用@Value的默认值
        String secretKey = customProperties != null && customProperties.getJwt() != null
                ? customProperties.getJwt().getSecret()
                : secret;
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 声明解析函数接口
     *
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }

    /**
     * 设置密钥（用于测试）
     *
     * @param secret 密钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 设置过期时间（用于测试）
     *
     * @param expiration 过期时间（毫秒）
     */
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    /**
     * 设置刷新令牌过期时间（用于测试）
     *
     * @param refreshExpiration 刷新令牌过期时间（毫秒）
     */
    public void setRefreshExpiration(Long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * 获取过期时间（毫秒）
     *
     * @return 过期时间
     */
    public Long getExpiration() {
        return customProperties != null && customProperties.getJwt() != null
                ? customProperties.getJwt().getExpiration()
                : expiration;
    }

    /**
     * 获取刷新令牌过期时间（毫秒）
     *
     * @return 刷新令牌过期时间
     */
    public Long getRefreshExpiration() {
        return customProperties != null && customProperties.getJwt() != null
                ? (customProperties.getJwt().getRefreshExpiration() != null
                ? customProperties.getJwt().getRefreshExpiration()
                : refreshExpiration)
                : refreshExpiration;
    }

    /**
     * 从请求中提取Token
     *
     * @param request HTTP请求
     * @return JWT Token，如果没有则返回null
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

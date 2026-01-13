package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 登录响应DTO - 增强版，包含刷新令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /**
     * JWT Access Token (短期有效)
     */
    private String token;

    /**
     * JWT Refresh Token (长期有效)
     */
    private String refreshToken;

    /**
     * Access Token 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * Refresh Token 过期时间（秒）
     */
    private Long refreshExpiresIn;

    /**
     * 用户信息
     */
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String username;
        private String phone;
        private String email;
        private String avatar;
        private Integer role_id;
        private String role_name;
    }

    /**
     * Token 类型：Bearer
     */
    private String tokenType = "Bearer";
}

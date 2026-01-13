package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.UserLoginDTO;
import com.zhouyi.dto.UserRegistDTO;
import com.zhouyi.dto.LoginResponseDTO;
import com.zhouyi.service.UserService;
import com.zhouyi.service.UserRoleService;
import com.zhouyi.entity.User;
import com.zhouyi.entity.UserRole;
import com.zhouyi.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

/**
 * 认证控制器 - 统一认证接口
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录接口 - RESTful标准
     *
     * @param loginDTO 用户登录DTO对象，包含手机号和密码
     * @return 登录结果，包含JWT Token
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        // 调用服务层进行登录验证
        Result<User> loginResult = userService.login(loginDTO.getPhone(), loginDTO.getPassword());

        if (loginResult.getCode() != 200) {
            return Result.error(loginResult.getMessage(), "/api/v1/auth/login");
        }

        User user = loginResult.getData();

        // 生成JWT Access Token和Refresh Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getPhone());

        // 构建登录响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getExpiration() / 1000); // 转换为秒
        response.setRefreshExpiresIn(jwtUtil.getRefreshExpiration() / 1000); // 转换为秒

        // 构建嵌套的用户信息
        LoginResponseDTO.UserInfo userInfo = new LoginResponseDTO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatarUrl());

        // 从数据库获取实际角色
        Integer roleId = 2; // 默认普通用户
        String roleName = "普通用户";

        var rolesResult = userRoleService.getRolesDetailsByUserId(user.getId());
        if (rolesResult.getCode() == 200 && rolesResult.getData() != null && !rolesResult.getData().isEmpty()) {
            roleId = rolesResult.getData().get(0).getId();
            roleName = rolesResult.getData().get(0).getRoleName();
        }

        userInfo.setRole_id(roleId);
        userInfo.setRole_name(roleName);

        response.setUser(userInfo);

        return Result.success("登录成功", response, "/api/v1/auth/login");
    }

    /**
     * 用户注册接口 - RESTful标准
     *
     * @param userRegistDTO 用户注册DTO对象，包含手机号、密码、用户名、邮箱
     * @return 注册结果
     */
    @PostMapping("/register")
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Result<?> register(@Valid @RequestBody UserRegistDTO userRegistDTO) {
        // 创建User对象
        User user = new User();
        user.setPhone(userRegistDTO.getPhone());
        user.setPassword(userRegistDTO.getPassword());
        user.setUsername(userRegistDTO.getUsername());
        user.setEmail(userRegistDTO.getEmail());

        // 调用服务层进行注册
        Result<String> registResult = userService.addUser(user);

        if (registResult.getCode() != 200) {
            return Result.error(registResult.getMessage(), "/api/v1/auth/register");
        }

        // 获取注册后的用户信息
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("user_id", user.getId());
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());

        return Result.created("注册成功", data, "/api/v1/auth/register");
    }

    /**
     * 验证token有效性
     *
     * @param token JWT token
     * @return 验证结果
     */
    @PostMapping("/validate")
    public Result<Boolean> validateToken(@RequestParam("token") String token) {
        try {
            boolean isValid = jwtUtil.validateToken(token);
            return Result.success("Token验证完成", isValid, "/api/v1/auth/validate");
        } catch (Exception e) {
            return Result.success("Token无效", false, "/api/v1/auth/validate");
        }
    }

    /**
     * 刷新token
     *
     * @param refreshTokenRequest 包含refresh token的请求体
     * @return 新的JWT token
     */
    @PostMapping("/refresh")
    public Result<LoginResponseDTO> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return Result.error("Refresh token不能为空", "/api/v1/auth/refresh");
            }

            // 验证refresh token是否有效且为refresh类型
            if (!jwtUtil.validateToken(refreshToken) ||
                    !jwtUtil.validateTokenType(refreshToken, "refresh")) {
                return Result.error("无效的refresh token", "/api/v1/auth/refresh");
            }

            // 从refresh token中获取用户信息
            String phone = jwtUtil.getPhoneFromToken(refreshToken);

            // 获取用户信息（验证用户仍然存在且有效）
            var userResult = userService.getUserByPhone(phone);
            if (userResult.getCode() != 200 || userResult.getData() == null) {
                return Result.error("用户不存在", "/api/v1/auth/refresh");
            }

            User user = userResult.getData();

            // 生成新的access token和refresh token
            String newAccessToken = jwtUtil.generateToken(user.getId(), user.getPhone());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getPhone());

            // 构建响应
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setExpiresIn(jwtUtil.getExpiration() / 1000);
            response.setRefreshExpiresIn(jwtUtil.getRefreshExpiration() / 1000);

            // 构建嵌套的用户信息
            LoginResponseDTO.UserInfo userInfo = new LoginResponseDTO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setPhone(user.getPhone());
            userInfo.setEmail(user.getEmail());
            userInfo.setAvatar(user.getAvatarUrl());

            // 获取实际角色
            Integer roleId = 2; // 默认普通用户
            String roleName = "普通用户";

            var rolesResult = userRoleService.getRolesDetailsByUserId(user.getId());
            if (rolesResult.getCode() == 200 && rolesResult.getData() != null && !rolesResult.getData().isEmpty()) {
                roleId = rolesResult.getData().get(0).getId();
                roleName = rolesResult.getData().get(0).getRoleName();
            }

            userInfo.setRole_id(roleId);
            userInfo.setRole_name(roleName);

            response.setUser(userInfo);

            return Result.success("Token刷新成功", response, "/api/v1/auth/refresh");
        } catch (Exception e) {
            return Result.error("Token刷新失败：" + e.getMessage(), "/api/v1/auth/refresh");
        }
    }

    /**
     * 刷新token（向后兼容接口）
     *
     * @return 新的JWT token
     */
    @PostMapping("/refresh/legacy")
    public Result<String> refreshTokenLegacy() {
        try {
            // 从SecurityContext获取当前用户信息
            String phone = SecurityContextHolder.getContext().getAuthentication().getName();

            // 获取用户信息
            var userResult = userService.getUserByPhone(phone);
            if (userResult.getCode() != 200 || userResult.getData() == null) {
                return Result.error("用户不存在", "/api/v1/auth/refresh/legacy");
            }

            User user = userResult.getData();

            // 生成新的token
            String newToken = jwtUtil.generateToken(user.getId(), user.getPhone());

            return Result.success("Token刷新成功", newToken, "/api/v1/auth/refresh/legacy");
        } catch (Exception e) {
            return Result.error("Token刷新失败：" + e.getMessage(), "/api/v1/auth/refresh/legacy");
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> getCurrentUser() {
        try {
            // 从SecurityContext获取当前用户信息
            String phone = SecurityContextHolder.getContext().getAuthentication().getName();

            // 获取用户信息
            var userResult = userService.getUserByPhone(phone);
            if (userResult.getCode() != 200 || userResult.getData() == null) {
                return Result.error("用户不存在", "/api/v1/auth/profile");
            }

            return Result.success("获取用户信息成功", userResult.getData(), "/api/v1/auth/profile");
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage(), "/api/v1/auth/profile");
        }
    }

    /**
     * 获取当前用户信息（向后兼容接口）
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUserLegacy() {
        // 直接调用新的接口实现，保持向后兼容
        return getCurrentUser();
    }

    /**
     * 登出操作（可选，主要用于记录日志等）
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        try {
            // 清除SecurityContext
            SecurityContextHolder.clearContext();
            return Result.successWithMessageAndPath("登出成功", "/api/v1/auth/logout");
        } catch (Exception e) {
            return Result.error("登出失败：" + e.getMessage(), "/api/v1/auth/logout");
        }
    }
}

package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.UserUpdateDTO;
import com.zhouyi.dto.PasswordUpdateDTO;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 用户控制器，用于处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户信息 - 从Authorization Bearer头获取
     * 
     * @param request HTTP请求
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        try {
            // 从Authorization Bearer头中获取token
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/profile");
            }

            String token = authorizationHeader.substring(7);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/profile");
            }

            // 获取用户信息
            return userService.getUserById(userId);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage(), "/api/v1/users/profile");
        }
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    /**
     * 获取用户列表 - RESTful标准GET方法
     * 
     * @param keywords 搜索关键词
     * @param status   用户状态
     * @param page     页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @GetMapping
    public Result<?> getUsers(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 调用服务层获取用户列表（需要实现分页查询）
            // 暂时返回所有用户，后续可以优化为分页查询
            return userService.getAllUsers();
        } catch (Exception e) {
            return Result.error("查询用户列表失败：" + e.getMessage(), "/api/v1/users");
        }
    }

    /**
     * 创建用户 - RESTful标准POST方法
     * 
     * @param user 用户对象
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        try {
            return userService.addUser(user);
        } catch (Exception e) {
            return Result.error("创建用户失败：" + e.getMessage(), "/api/v1/users");
        }
    }

    /**
     * 更新用户 - RESTful标准PUT方法
     * 
     * @param id            用户ID
     * @param userUpdateDTO 用户更新DTO对象，包含用户信息和校验
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Integer id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            // 将DTO转换为Entity
            User user = new User();
            user.setId(id);
            user.setPhone(userUpdateDTO.getPhone());
            user.setPassword(userUpdateDTO.getPassword());
            user.setUsername(userUpdateDTO.getUsername());
            user.setEmail(userUpdateDTO.getEmail());
            user.setStatus(userUpdateDTO.getStatus());

            return userService.updateUser(user);
        } catch (Exception e) {
            return Result.error("更新用户失败：" + e.getMessage(), "/api/v1/users/" + id);
        }
    }

    /**
     * 更新用户密码接口 - 使用Authorization Bearer认证
     * 
     * @param passwordUpdateDTO 密码更新DTO对象
     * @param request           HTTP请求
     * @return 更新结果
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO,
            HttpServletRequest request) {
        try {
            // 1. 验证新密码和确认密码是否一致
            if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
                return Result.error("新密码和确认密码不一致", "/api/v1/users/password");
            }

            // 2. 从Authorization Bearer头中获取当前用户信息
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/password");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/password");
            }

            // 3. 设置当前用户ID到DTO中
            passwordUpdateDTO.setUserId(currentUserId);

            // 4. 调用服务层更新密码
            Result<String> updateResult = userService.updateUserPassword(passwordUpdateDTO);

            return updateResult;

        } catch (Exception e) {
            return Result.error("密码更新失败：" + e.getMessage(), "/api/v1/users/password");
        }
    }

    /**
     * 删除用户接口
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return Result.error("用户ID不能为空或小于等于0", "/api/v1/users/" + id);
            }

            return userService.deleteUser(id);

        } catch (Exception e) {
            return Result.error("删除用户失败：" + e.getMessage(), "/api/v1/users/" + id);
        }
    }

    /**
     * 更新用户个人资料 - 用户自己操作
     * 
     * @param id         用户ID
     * @param profileDTO 个人资料DTO
     * @param request    HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}/profile")
    public Result<String> updateUserProfile(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.UserProfileUpdateDTO profileDTO,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/" + id + "/profile");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/" + id + "/profile");
            }

            if (!currentUserId.equals(id)) {
                return Result.error("无权修改其他用户的个人资料", "/api/v1/users/" + id + "/profile");
            }

            return userService.updateUserProfileByUser(id, profileDTO.getUsername(), profileDTO.getAvatar());
        } catch (Exception e) {
            return Result.error("更新个人资料失败：" + e.getMessage(), "/api/v1/users/" + id + "/profile");
        }
    }

    /**
     * 更新用户属性 - 管理员操作
     * 
     * @param id       用户ID
     * @param adminDTO 管理员更新DTO
     * @return 更新结果
     */
    @PutMapping("/{id}/admin")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUserAttributes(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.AdminUserUpdateDTO adminDTO) {
        try {
            return userService.updateUserAttributesByAdmin(id, adminDTO.getStatus(), adminDTO.getRoleId());
        } catch (Exception e) {
            return Result.error("更新用户属性失败：" + e.getMessage(), "/api/v1/users/" + id + "/admin");
        }
    }

    /**
     * 请求邮箱更新
     * 
     * @param id       用户ID
     * @param emailDTO 邮箱更新请求DTO
     * @param request  HTTP请求
     * @return 请求结果
     */
    @PostMapping("/{id}/email/request")
    public Result<String> requestEmailUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.EmailUpdateRequestDTO emailDTO,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/" + id + "/email/request");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/" + id + "/email/request");
            }

            if (!currentUserId.equals(id)) {
                return Result.error("无权修改其他用户的邮箱", "/api/v1/users/" + id + "/email/request");
            }

            return userService.requestEmailUpdate(id, emailDTO.getNewEmail());
        } catch (Exception e) {
            return Result.error("邮箱更新请求失败：" + e.getMessage(), "/api/v1/users/" + id + "/email/request");
        }
    }

    /**
     * 验证并更新邮箱
     * 
     * @param id              用户ID
     * @param verificationDTO 验证码DTO
     * @param request         HTTP请求
     * @return 验证结果
     */
    @PostMapping("/{id}/email/verify")
    public Result<String> verifyEmailUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.VerificationDTO verificationDTO,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/" + id + "/email/verify");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/" + id + "/email/verify");
            }

            if (!currentUserId.equals(id)) {
                return Result.error("无权修改其他用户的邮箱", "/api/v1/users/" + id + "/email/verify");
            }

            return userService.verifyAndUpdateEmail(id, verificationDTO.getToken());
        } catch (Exception e) {
            return Result.error("邮箱验证失败：" + e.getMessage(), "/api/v1/users/" + id + "/email/verify");
        }
    }

    /**
     * 请求手机号更新
     * 
     * @param id       用户ID
     * @param phoneDTO 手机号更新请求DTO
     * @param request  HTTP请求
     * @return 请求结果
     */
    @PostMapping("/{id}/phone/request")
    public Result<String> requestPhoneUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.PhoneUpdateRequestDTO phoneDTO,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/" + id + "/phone/request");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/" + id + "/phone/request");
            }

            if (!currentUserId.equals(id)) {
                return Result.error("无权修改其他用户的手机号", "/api/v1/users/" + id + "/phone/request");
            }

            return userService.requestPhoneUpdate(id, phoneDTO.getNewPhone());
        } catch (Exception e) {
            return Result.error("手机号更新请求失败：" + e.getMessage(), "/api/v1/users/" + id + "/phone/request");
        }
    }

    /**
     * 验证并更新手机号
     * 
     * @param id              用户ID
     * @param verificationDTO 验证码DTO
     * @param request         HTTP请求
     * @return 验证结果
     */
    @PostMapping("/{id}/phone/verify")
    public Result<String> verifyPhoneUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.VerificationDTO verificationDTO,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/users/" + id + "/phone/verify");
            }

            String token = authorizationHeader.substring(7);
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            if (currentUserId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/users/" + id + "/phone/verify");
            }

            if (!currentUserId.equals(id)) {
                return Result.error("无权修改其他用户的手机号", "/api/v1/users/" + id + "/phone/verify");
            }

            return userService.verifyAndUpdatePhone(id, verificationDTO.getToken());
        } catch (Exception e) {
            return Result.error("手机号验证失败：" + e.getMessage(), "/api/v1/users/" + id + "/phone/verify");
        }
    }
}

package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.UserUpdateDTO;
import com.zhouyi.dto.PasswordUpdateDTO;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.zhouyi.common.security.CustomUserDetails;
import jakarta.validation.Valid;

/**
 * 用户控制器，用于处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息 - 从 SecurityContext 获取
     * 
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userService.getUserById(userDetails.getUserId());
    }

    /**
     * 根据ID查询用户 - 只允许查询本人或管理员
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("@authService.isOwnerOrAdmin(#id)")
    public Result<User> getUserById(@PathVariable("id") Integer id) {
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
        return userService.getAllUsers();
    }

    /**
     * 创建用户 - RESTful标准POST方法
     * 
     * @param user 用户对象
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * 更新用户 - RESTful标准PUT方法 - 只允许更新本人或管理员
     * 
     * @param id            用户ID
     * @param userUpdateDTO 用户更新DTO对象，包含用户信息和校验
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("@authService.isOwnerOrAdmin(#id)")
    public Result<String> updateUser(@PathVariable Integer id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User user = new User();
        user.setId(id);
        user.setPhone(userUpdateDTO.getPhone());
        user.setPassword(userUpdateDTO.getPassword());
        user.setUsername(userUpdateDTO.getUsername());
        user.setEmail(userUpdateDTO.getEmail());
        user.setStatus(userUpdateDTO.getStatus());

        return userService.updateUser(user);
    }

    /**
     * 更新用户密码接口
     * 
     * @param passwordUpdateDTO 密码更新DTO对象
     * @return 更新结果
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
            return Result.error("新密码和确认密码不一致");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        passwordUpdateDTO.setUserId(userDetails.getUserId());
        return userService.updateUserPassword(passwordUpdateDTO);
    }

    /**
     * 删除用户接口
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@authService.isOwnerOrAdmin(#id)")
    public Result<String> deleteUser(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("用户ID无效");
        }
        return userService.deleteUser(id);
    }

    /**
     * 更新用户个人资料
     * 
     * @param id         用户ID
     * @param profileDTO 个人资料DTO
     * @return 更新结果
     */
    @PutMapping("/{id}/profile")
    public Result<String> updateUserProfile(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.UserProfileUpdateDTO profileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUserId().equals(id)) {
            return Result.error(403, "无权修改其他用户的个人资料");
        }

        return userService.updateUserProfileByUser(id, profileDTO.getUsername(), profileDTO.getAvatar());
    }

    /**
     * 更新用户属性 - 管理员操作
     * 
     * @param id       用户ID
     * @param adminDTO 管理员更新DTO
     * @return 更新结果
     */
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUserAttributes(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.AdminUserUpdateDTO adminDTO) {
        return userService.updateUserAttributesByAdmin(id, adminDTO.getStatus(), adminDTO.getRoleId());
    }

    /**
     * 请求邮箱更新
     * 
     * @param id       用户ID
     * @param emailDTO 邮箱更新请求DTO
     * @return 请求结果
     */
    @PostMapping("/{id}/email/request")
    public Result<String> requestEmailUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.EmailUpdateRequestDTO emailDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUserId().equals(id)) {
            return Result.error(403, "无权修改其他用户的邮箱");
        }

        return userService.requestEmailUpdate(id, emailDTO.getNewEmail());
    }

    /**
     * 验证并更新邮箱
     * 
     * @param id              用户ID
     * @param verificationDTO 验证码DTO
     * @return 验证结果
     */
    @PostMapping("/{id}/email/verify")
    public Result<String> verifyEmailUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.VerificationDTO verificationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUserId().equals(id)) {
            return Result.error(403, "无权修改其他用户的邮箱");
        }

        return userService.verifyAndUpdateEmail(id, verificationDTO.getToken());
    }

    /**
     * 请求手机号更新
     * 
     * @param id       用户ID
     * @param phoneDTO 手机号更新请求DTO
     * @return 请求结果
     */
    @PostMapping("/{id}/phone/request")
    public Result<String> requestPhoneUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.PhoneUpdateRequestDTO phoneDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUserId().equals(id)) {
            return Result.error(403, "无权修改其他用户的手机号");
        }

        return userService.requestPhoneUpdate(id, phoneDTO.getNewPhone());
    }

    /**
     * 验证并更新手机号
     * 
     * @param id              用户ID
     * @param verificationDTO 验证码DTO
     * @return 验证结果
     */
    @PostMapping("/{id}/phone/verify")
    public Result<String> verifyPhoneUpdate(@PathVariable Integer id,
            @Valid @RequestBody com.zhouyi.dto.VerificationDTO verificationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUserId().equals(id)) {
            return Result.error(403, "无权修改其他用户的手机号");
        }

        return userService.verifyAndUpdatePhone(id, verificationDTO.getToken());
    }
}

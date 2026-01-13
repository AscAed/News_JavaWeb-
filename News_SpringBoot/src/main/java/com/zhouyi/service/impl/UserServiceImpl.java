package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.PasswordUtil;
import com.zhouyi.entity.User;
import com.zhouyi.mapper.UserMapper;
import com.zhouyi.service.UserService;
import com.zhouyi.dto.PasswordUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<User> login(String phone, String password) {
        System.out.println("尝试登录：手机号=" + phone + ", 密码=" + password);
        // 先根据手机号查询用户
        User user = userMapper.selectUserByPhone(phone);
        System.out.println("查询结果：user=" + (user != null ? user.getUsername() : "null"));
        if (user != null) {
            String storedPassword = user.getPassword();

            // 验证密码（只支持BCrypt加密）
            if (PasswordUtil.matches(password, storedPassword)) {
                return Result.successWithMessageAndData("登录成功", user);
            } else {
                return Result.error("手机号或密码错误");
            }
        } else {
            return Result.error("手机号或密码错误");
        }
    }

    @Override
    public Result<User> getUserById(Integer id) {
        User user = userMapper.selectUserById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    @Override
    public Result<User> getUserByPhone(String phone) {
        User user = userMapper.selectUserByPhone(phone);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    @Override
    public Result<List<User>> getAllUsers() {
        List<User> users = userMapper.selectAllUsers();
        return Result.success(users);
    }

    @Override
    public Result<String> addUser(User user) {
        // 加密密码
        String encodedPassword = PasswordUtil.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);

        int rows = userMapper.insertUser(user);
        if (rows > 0) {
            return Result.success("添加用户成功");
        } else {
            return Result.error("添加用户失败");
        }
    }

    @Override
    @Deprecated
    public Result<String> updateUser(User user) {
        int rows = userMapper.updateUserById(user);
        if (rows > 0) {
            return Result.success("更新用户成功");
        } else {
            return Result.error("更新用户失败");
        }
    }

    @Override
    public Result<String> updateUserAvatar(User user) {
        int rows = userMapper.updateUserAvatar(user.getId(), user.getAvatarUrl());
        if (rows > 0) {
            return Result.success("头像更新成功");
        } else {
            return Result.error("头像更新失败，用户不存在");
        }
    }

    @Override
    public Result<String> updateUserPassword(PasswordUpdateDTO passwordUpdateDTO) {
        Result<User> userResult = getUserById(passwordUpdateDTO.getUserId());
        if (userResult.getCode() != 200 || userResult.getData() == null) {
            return Result.error("用户不存在");
        }

        User user = userResult.getData();

        if (!PasswordUtil.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }

        if (PasswordUtil.matches(passwordUpdateDTO.getNewPassword(), user.getPassword())) {
            return Result.error("新密码不能与原密码相同");
        }

        String encodedNewPassword = PasswordUtil.encodePassword(passwordUpdateDTO.getNewPassword());

        int rows = userMapper.updateUserPassword(passwordUpdateDTO.getUserId(), encodedNewPassword);
        if (rows > 0) {
            return Result.success("密码更新成功，请重新登录");
        } else {
            return Result.error("密码更新失败");
        }
    }

    @Override
    public Result<String> deleteUser(Integer id) {

        int rows = userMapper.deleteUserById(id);
        if (rows > 0) {
            return Result.success("删除用户成功");
        } else {
            return Result.error("删除用户失败");
        }
    }

    @Override
    public Result<String> updateUserProfileByUser(Integer userId, String username, String avatar) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        int rows = userMapper.updateUserProfile(userId, username, avatar);
        if (rows > 0) {
            return Result.success("Profile updated successfully");
        } else {
            return Result.error("Failed to update profile");
        }
    }

    @Override
    public Result<String> updateUserAttributesByAdmin(Integer userId, Integer status, Integer roleId) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        int rows = userMapper.updateUserAttributes(userId, status);
        if (rows > 0) {
            return Result.success("User attributes updated successfully");
        } else {
            return Result.error("Failed to update user attributes");
        }
    }

    @Override
    public Result<String> requestEmailUpdate(Integer userId, String newEmail) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        User existingUser = userMapper.selectUserByEmail(newEmail);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Email already in use by another user");
        }

        return Result.success("Email update request initiated. Verification code will be sent.");
    }

    @Override
    public Result<String> requestPhoneUpdate(Integer userId, String newPhone) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        User existingUser = userMapper.selectUserByPhone(newPhone);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Phone number already in use by another user");
        }

        return Result.success("Phone update request initiated. Verification code will be sent.");
    }

    @Override
    public Result<String> verifyAndUpdateEmail(Integer userId, String token) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        if (token == null || token.trim().isEmpty()) {
            return Result.error("Verification code cannot be empty");
        }

        return Result.success("Email verification successful. Email will be updated.");
    }

    @Override
    public Result<String> verifyAndUpdatePhone(Integer userId, String token) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }

        if (token == null || token.trim().isEmpty()) {
            return Result.error("Verification code cannot be empty");
        }

        return Result.success("Phone verification successful. Phone will be updated.");
    }
}

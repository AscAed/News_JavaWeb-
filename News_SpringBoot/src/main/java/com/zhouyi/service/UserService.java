package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.entity.User;
import com.zhouyi.dto.PasswordUpdateDTO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录：根据手机号和密码查询用户
     * 
     * @param phone    手机号
     * @param password 密码
     * @return 登录结果，包含用户对象或错误信息
     */
    Result<User> login(String phone, String password);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 查询结果
     */
    Result<User> getUserById(Integer id);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 查询结果
     */
    Result<User> getUserByPhone(String phone);

    /**
     * 查询所有用户
     * 
     * @return 用户列表结果
     */
    Result<List<User>> getAllUsers();

    /**
     * 添加用户
     * 
     * @param user 用户对象
     * @return 添加结果
     */
    Result<String> addUser(User user);

    /**
     * 更新用户头像
     * 
     * @param user 用户对象（包含ID和头像URL）
     * @return 更新结果
     */
    Result<String> updateUserAvatar(User user);

    /**
     * 更新用户密码
     * 
     * @param passwordUpdateDTO 密码更新DTO对象
     * @return 更新结果
     */
    Result<String> updateUserPassword(PasswordUpdateDTO passwordUpdateDTO);

    /**
     * 更新用户
     * 
     * @param user 用户对象
     * @return 更新结果
     * @deprecated Use updateUserProfileByUser or updateUserAttributesByAdmin
     *             instead
     */
    @Deprecated
    Result<String> updateUser(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    Result<String> deleteUser(Integer id);

    /**
     * 更新用户个人资料（用户自己操作）
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param avatar   头像URL
     * @return 更新结果
     */
    Result<String> updateUserProfileByUser(Integer userId, String username, String avatar);

    /**
     * 更新用户属性（管理员操作）
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param roleId 角色ID
     * @return 更新结果
     */
    Result<String> updateUserAttributesByAdmin(Integer userId, Integer status, Integer roleId);

    /**
     * 请求邮箱更新
     * 
     * @param userId   用户ID
     * @param newEmail 新邮箱
     * @return 请求结果
     */
    Result<String> requestEmailUpdate(Integer userId, String newEmail);

    /**
     * 请求手机号更新
     * 
     * @param userId   用户ID
     * @param newPhone 新手机号
     * @return 请求结果
     */
    Result<String> requestPhoneUpdate(Integer userId, String newPhone);

    /**
     * 验证并更新邮箱
     * 
     * @param userId 用户ID
     * @param token  验证码
     * @return 验证结果
     */
    Result<String> verifyAndUpdateEmail(Integer userId, String token);

    /**
     * 验证并更新手机号
     * 
     * @param userId 用户ID
     * @param token  验证码
     * @return 验证结果
     */
    Result<String> verifyAndUpdatePhone(Integer userId, String token);
}

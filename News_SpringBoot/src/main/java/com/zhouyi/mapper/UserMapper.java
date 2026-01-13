package com.zhouyi.mapper;

import com.zhouyi.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口，用于操作users表
 */
@Mapper
public interface UserMapper {

    /**
     * 根据手机号和密码查询用户（用于登录）
     * 
     * @param phone    手机号
     * @param password 密码
     * @return 用户对象，如果不存在则返回null
     */
    User selectUserByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    User selectUserById(Integer id);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户对象
     */
    User selectUserByPhone(@Param("phone") String phone);

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> selectAllUsers();

    /**
     * 插入新用户
     * 
     * @param user 用户对象
     * @return 影响行数
     */
    int insertUser(User user);

    /**
     * 根据ID更新用户
     * 
     * @param user 用户对象
     * @return 影响行数
     */
    int updateUserById(User user);

    /**
     * 根据ID更新用户头像
     * 
     * @param userId    用户ID
     * @param avatarUrl 头像URL
     * @return 影响行数
     */
    int updateUserAvatar(@Param("userId") Integer userId, @Param("avatarUrl") String avatarUrl);

    /**
     * 根据ID更新用户密码
     * 
     * @param userId   用户ID
     * @param password 加密后的密码
     * @return 影响行数
     */
    int updateUserPassword(@Param("userId") Integer userId, @Param("password") String password);

    /**
     * 根据ID删除用户
     * 
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteUserById(Integer id);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户对象
     */
    User selectUserByEmail(@Param("email") String email);

    /**
     * 更新用户个人资料（用户名和头像）
     * 
     * @param userId    用户ID
     * @param username  用户名
     * @param avatarUrl 头像URL
     * @return 影响行数
     */
    int updateUserProfile(@Param("userId") Integer userId,
            @Param("username") String username,
            @Param("avatarUrl") String avatarUrl);

    /**
     * 更新用户属性（管理员操作：状态和角色）
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateUserAttributes(@Param("userId") Integer userId,
            @Param("status") Integer status);

    /**
     * 更新用户邮箱
     * 
     * @param userId 用户ID
     * @param email  邮箱
     * @return 影响行数
     */
    int updateUserEmail(@Param("userId") Integer userId, @Param("email") String email);

    /**
     * 更新用户手机号
     * 
     * @param userId 用户ID
     * @param phone  手机号
     * @return 影响行数
     */
    int updateUserPhone(@Param("userId") Integer userId, @Param("phone") String phone);
}

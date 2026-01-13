package com.zhouyi.mapper;

import com.zhouyi.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 用户角色关联Mapper接口，用于操作user_roles表
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 根据用户ID查询用户拥有的角色及其详情
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<com.zhouyi.entity.Role> selectRolesByUserId(@org.apache.ibatis.annotations.Param("userId") Integer userId);

    /**
     * 根据用户ID查询用户角色关联
     * 
     * @param userId 用户ID
     * @return 关联列表
     */
    List<UserRole> selectUserRolesByUserId(@org.apache.ibatis.annotations.Param("userId") Integer userId);

    /**
     * 根据角色ID查询用户角色关联
     * 
     * @param roleId 角色ID
     * @return 关联列表
     */
    List<UserRole> selectUserRolesByRoleId(Integer roleId);

    /**
     * 查询所有用户角色关联
     * 
     * @return 关联列表
     */
    List<UserRole> selectAllUserRoles();

    /**
     * 插入新用户角色关联
     * 
     * @param userRole 关联对象
     * @return 影响行数
     */
    int insertUserRole(UserRole userRole);

    /**
     * 根据ID删除用户角色关联
     * 
     * @param id 关联ID
     * @return 影响行数
     */
    int deleteUserRoleById(Integer id);

    /**
     * 根据用户ID和角色ID删除关联
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteUserRoleByUserIdAndRoleId(Integer userId, Integer roleId);
}

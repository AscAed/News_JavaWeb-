package com.zhouyi.service;

import com.zhouyi.entity.UserRole;
import com.zhouyi.common.result.Result;
import java.util.List;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService {

    /**
     * 根据用户ID查询角色关联
     * 
     * @param userId 用户ID
     * @return 关联列表结果
     */
    Result<List<UserRole>> getRolesByUserId(Integer userId);

    /**
     * 根据用户ID获取角色详情列表
     * 
     * @param userId 用户ID
     * @return 角色列表结果
     */
    Result<List<com.zhouyi.entity.Role>> getRolesDetailsByUserId(Integer userId);

    /**
     * 根据角色ID查询用户关联
     * 
     * @param roleId 角色ID
     * @return 关联列表结果
     */
    Result<List<UserRole>> getUsersByRoleId(Integer roleId);

    /**
     * 查询所有关联
     * 
     * @return 关联列表结果
     */
    Result<List<UserRole>> getAllUserRoles();

    /**
     * 添加用户角色关联
     * 
     * @param userRole 关联对象
     * @return 添加结果
     */
    Result<String> addUserRole(UserRole userRole);

    /**
     * 删除关联
     * 
     * @param id 关联ID
     * @return 删除结果
     */
    Result<String> deleteUserRole(Integer id);

    /**
     * 根据用户ID和角色ID删除关联
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 删除结果
     */
    Result<String> deleteUserRoleByUserAndRole(Integer userId, Integer roleId);
}

package com.zhouyi.service;

import com.zhouyi.entity.Role;
import com.zhouyi.common.result.Result;
import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 查询结果
     */
    Result<Role> getRoleById(Integer id);

    /**
     * 根据名称查询角色
     * @param roleName 角色名称
     * @return 查询结果
     */
    Result<Role> getRoleByName(String roleName);

    /**
     * 查询所有角色
     * @return 角色列表结果
     */
    Result<List<Role>> getAllRoles();

    /**
     * 添加角色
     * @param role 角色对象
     * @return 添加结果
     */
    Result<String> addRole(Role role);

    /**
     * 更新角色
     * @param role 角色对象
     * @return 更新结果
     */
    Result<String> updateRole(Role role);

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    Result<String> deleteRole(Integer id);
}

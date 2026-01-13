package com.zhouyi.mapper;

import com.zhouyi.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 角色Mapper接口，用于操作roles表
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色对象
     */
    Role selectRoleById(Integer id);

    /**
     * 根据角色名称查询角色
     * @param roleName 角色名称
     * @return 角色对象
     */
    Role selectRoleByName(String roleName);

    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<Role> selectAllRoles();

    /**
     * 插入新角色
     * @param role 角色对象
     * @return 影响行数
     */
    int insertRole(Role role);

    /**
     * 根据ID更新角色
     * @param role 角色对象
     * @return 影响行数
     */
    int updateRoleById(Role role);

    /**
     * 根据ID删除角色
     * @param id 角色ID
     * @return 影响行数
     */
    int deleteRoleById(Integer id);
}

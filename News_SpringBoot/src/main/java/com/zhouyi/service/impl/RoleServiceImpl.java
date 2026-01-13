package com.zhouyi.service.impl;

import com.zhouyi.entity.Role;
import com.zhouyi.mapper.RoleMapper;
import com.zhouyi.service.RoleService;
import com.zhouyi.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Result<Role> getRoleById(Integer id) {
        try {
            Role role = roleMapper.selectRoleById(id);
            if (role != null) {
                return Result.success(role);
            } else {
                return Result.error("角色不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Role> getRoleByName(String roleName) {
        try {
            Role role = roleMapper.selectRoleByName(roleName);
            if (role != null) {
                return Result.success(role);
            } else {
                return Result.error("角色不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleMapper.selectAllRoles();
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> addRole(Role role) {
        try {
            int rows = roleMapper.insertRole(role);
            if (rows > 0) {
                return Result.success("添加角色成功");
            } else {
                return Result.error("添加角色失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> updateRole(Role role) {
        try {
            int rows = roleMapper.updateRoleById(role);
            if (rows > 0) {
                return Result.success("更新角色成功");
            } else {
                return Result.error("更新角色失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteRole(Integer id) {
        try {
            int rows = roleMapper.deleteRoleById(id);
            if (rows > 0) {
                return Result.success("删除角色成功");
            } else {
                return Result.error("删除角色失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}

package com.zhouyi.service.impl;

import com.zhouyi.entity.UserRole;
import com.zhouyi.mapper.UserRoleMapper;
import com.zhouyi.service.UserRoleService;
import com.zhouyi.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<List<UserRole>> getRolesByUserId(Integer userId) {
        try {
            List<UserRole> userRoles = userRoleMapper.selectUserRolesByUserId(userId);
            return Result.success(userRoles);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<com.zhouyi.entity.Role>> getRolesDetailsByUserId(Integer userId) {
        try {
            List<com.zhouyi.entity.Role> roles = userRoleMapper.selectRolesByUserId(userId);
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<UserRole>> getUsersByRoleId(Integer roleId) {
        try {
            List<UserRole> userRoles = userRoleMapper.selectUserRolesByRoleId(roleId);
            return Result.success(userRoles);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<UserRole>> getAllUserRoles() {
        try {
            List<UserRole> userRoles = userRoleMapper.selectAllUserRoles();
            return Result.success(userRoles);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> addUserRole(UserRole userRole) {
        try {
            int rows = userRoleMapper.insertUserRole(userRole);
            if (rows > 0) {
                return Result.success("添加关联成功");
            } else {
                return Result.error("添加关联失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteUserRole(Integer id) {
        try {
            int rows = userRoleMapper.deleteUserRoleById(id);
            if (rows > 0) {
                return Result.success("删除关联成功");
            } else {
                return Result.error("删除关联失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteUserRoleByUserAndRole(Integer userId, Integer roleId) {
        try {
            int rows = userRoleMapper.deleteUserRoleByUserIdAndRoleId(userId, roleId);
            if (rows > 0) {
                return Result.success("删除关联成功");
            } else {
                return Result.error("删除关联失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}

package com.zhouyi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 角色实体类，对应roles表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Integer id; // 角色ID
    private String roleName; // 角色名称（映射到role_name字段）
    private String description; // 角色描述
    private Integer status; // 状态：0-禁用，1-启用

}

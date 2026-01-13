package com.zhouyi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

/**
 * 用户角色关联实体类，对应user_roles表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    private Integer id;          // 关联ID
    private Integer userId;      // 用户ID（映射到user_id字段）
    private Integer roleId;      // 角色ID（映射到role_id字段）
    private Date createdTime;    // 创建时间
}

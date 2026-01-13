package com.zhouyi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

/**
 * 用户实体类，对应users表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;              // 用户ID
    private String phone;            // 手机号
    private String password;         // 密码
    private String username;         // 用户名
    private String email;            // 邮箱
    private String avatarUrl;        // 头像URL
    private Integer status;          // 状态：0-禁用，1-启用
    private Date lastLoginTime;      // 最后登录时间
    private Date createdTime;         // 创建时间
    private Date updatedTime;         // 更新时间
}

package com.zhouyi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户个人资料DTO，包含角色信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Integer id;
    private String username;
    private String phone;
    private String email;
    private String avatar; // 对应前端使用的 avatar 字段
    private Integer role_id;
    private String role_name;
    private Integer status;
    private Date createdTime;
}

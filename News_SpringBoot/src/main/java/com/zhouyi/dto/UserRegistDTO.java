package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 用户注册DTO，用于参数校验
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[0-9]{11}$", message = "手机号格式错误，必须为11位纯数字")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,16}$", message = "密码格式错误，必须为6-16位，包含大小写字母和数字")
    private String password;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$", message = "用户名必须为2-20字符，可包含中英文、数字和下划线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;
}

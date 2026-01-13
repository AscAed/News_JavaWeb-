package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.UserUpdateDTO;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器测试类
 */
@RestController
@RequestMapping("/api/v1/user/test")
public class UserTestController {

    @Autowired
    private UserService userService;

    /**
     * 测试更新用户参数校验
     * @param userUpdateDTO 用户更新DTO
     * @return 更新结果
     */
    @PutMapping("/update-validation")
    public Result<String> testUpdateValidation(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        // 将DTO转换为Entity
        User user = new User();
        user.setId(userUpdateDTO.getId());
        user.setPhone(userUpdateDTO.getPhone());
        user.setPassword(userUpdateDTO.getPassword());
        user.setUsername(userUpdateDTO.getUsername());
        user.setEmail(userUpdateDTO.getEmail());
        user.setStatus(userUpdateDTO.getStatus());
        
        return userService.updateUser(user);
    }
}

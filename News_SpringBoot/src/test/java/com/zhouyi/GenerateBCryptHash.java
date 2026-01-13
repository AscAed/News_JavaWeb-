package com.zhouyi;

import com.zhouyi.common.utils.PasswordUtil;

/**
 * 生成BCrypt哈希
 */
public class GenerateBCryptHash {
    public static void main(String[] args) {
        String plainPassword = "Password123";
        String hash = PasswordUtil.encodePassword(plainPassword);
        
        System.out.println("明文密码: " + plainPassword);
        System.out.println("BCrypt哈希: " + hash);
        
        // 验证哈希
        boolean matches = PasswordUtil.matches(plainPassword, hash);
        System.out.println("验证结果: " + matches);
    }
}

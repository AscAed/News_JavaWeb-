package com.zhouyi.service;

import com.zhouyi.common.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 权限校验辅助服务，用于Spring Security表达式
 */
@Service("authService")
public class SecurityAuthService {

    /**
     * 检查当前用户是否为指定ID的所有者或管理员
     *
     * @param userId 目标用户ID
     * @return 是否有权操作
     */
    public boolean isOwnerOrAdmin(Integer userId) {
        if (userId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            // 如果是管理员，放行
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return true;
            }
            // 如果是本人，放行
            return userId.equals(userDetails.getUserId());
        }

        return false;
    }
}

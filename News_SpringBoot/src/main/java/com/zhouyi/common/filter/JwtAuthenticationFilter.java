package com.zhouyi.common.filter;

import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.entity.Role;
import com.zhouyi.service.UserRoleService;
import com.zhouyi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * JWT认证过滤器 - 统一Authorization Bearer Token认证
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        logger.info("JWT Filter called for: " + request.getMethod() + " " + request.getRequestURI());

        // 从标准Authorization头中获取token
        final String authorizationHeader = request.getHeader("Authorization");

        String phone = null;
        String jwtToken = null;

        logger.info("Authorization header: " + (authorizationHeader != null
                ? authorizationHeader.substring(0, Math.min(20, authorizationHeader.length())) + "..."
                : "null"));

        // 只支持标准的Bearer Token格式
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                phone = jwtUtil.getPhoneFromToken(jwtToken);
                logger.info("Extracted phone from token: " + phone);
            } catch (Exception e) {
                logger.warn("无法获取JWT Token或JWT Token已过期: " + e.getMessage());
            }
        } else {
            logger.warn("No valid Bearer token found in Authorization header");
        }

        // 验证token并设置认证信息
        if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 检查是否在 Redis 黑名单中
                Boolean isBlacklisted = redisTemplate.hasKey("jwt:blacklist:" + jwtToken);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    logger.warn("JWT Token 在黑名单中，已被撤销: " + phone);
                } else if (jwtUtil.validateToken(jwtToken)) {
                    logger.info("JWT Token验证成功，用户: " + phone);

                    // 获取用户角色
                    List<SimpleGrantedAuthority> authorities = getUserAuthorities(phone);
                    logger.info("用户权限数量: " + authorities.size());

                    // 从Token中获取用户ID
                    Integer userId = jwtUtil.getUserIdFromToken(jwtToken);

                    // 构建自定义用户详情，包含用户ID
                    com.zhouyi.common.security.CustomUserDetails userDetails = new com.zhouyi.common.security.CustomUserDetails(
                            userId,
                            phone,
                            "", // 密码不需要，因为已经通过JWT验证
                            authorities
                    );

                    // 创建认证token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("用户认证成功: " + phone + " (ID: " + userId + "), 权限: " +
                            authorities.stream().map(SimpleGrantedAuthority::getAuthority)
                                    .collect(Collectors.joining(", ")));
                } else {
                    logger.warn("JWT Token验证失败，用户: " + phone);
                }
            } catch (Exception e) {
                logger.error("JWT Token处理异常: " + e.getMessage(), e);
            }
        } else {
            if (phone == null) {
                logger.warn("无法从JWT Token中获取用户信息");
            } else {
                logger.debug("用户已认证，跳过: " + phone);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取用户权限列表
     */
    private List<SimpleGrantedAuthority> getUserAuthorities(String phone) {
        try {
            logger.info("开始获取用户权限，手机号: " + phone);

            // 根据手机号获取用户ID
            var userResult = userService.getUserByPhone(phone);

            if (userResult == null) {
                logger.warn("用户查询结果为null: " + phone);
                return List.of();
            }

            logger.info("用户查询结果: " + (userResult.isSuccess() ? "成功" : "失败"));

            if (!userResult.isSuccess() || userResult.getData() == null) {
                logger.warn("用户不存在: " + phone + ", 查询结果: " + userResult.getMessage());
                return List.of();
            }

            Integer userId = userResult.getData().getId();
            logger.info("用户ID: " + userId);

            // 获取用户角色
            var rolesResult = userRoleService.getRolesDetailsByUserId(userId);
            logger.info("角色查询结果: " + (rolesResult.isSuccess() ? "成功" : "失败"));

            if (rolesResult.isSuccess() && rolesResult.getData() != null) {
                List<Role> roles = rolesResult.getData();
                logger.info("用户角色数量: " + roles.size());

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> {
                            String authority = "ROLE_" + role.getRoleName().toUpperCase();
                            logger.info("添加权限: " + authority + " (角色ID: " + role.getId() + ", 角色名: "
                                    + role.getRoleName() + ")");
                            return new SimpleGrantedAuthority(authority);
                        })
                        .collect(Collectors.toList());

                logger.info("用户 " + phone + " 的角色: " +
                        authorities.stream().map(SimpleGrantedAuthority::getAuthority)
                                .collect(Collectors.joining(", ")));

                return authorities;
            }

            logger.warn("用户 " + phone + " 没有分配角色，使用默认USER角色");
            // 如果没有角色，默认赋予USER角色
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));

        } catch (Exception e) {
            logger.error("获取用户权限失败: " + e.getMessage(), e);
            return List.of();
        }
    }
}

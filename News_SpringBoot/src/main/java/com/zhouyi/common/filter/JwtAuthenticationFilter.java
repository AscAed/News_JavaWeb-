package com.zhouyi.common.filter;

import com.zhouyi.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器 - 统一Authorization Bearer Token认证
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // 从标准Authorization头中获取token
        final String authorizationHeader = request.getHeader("Authorization");
        
        String phone = null;
        String jwtToken = null;
        
        // 只支持标准的Bearer Token格式
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                phone = jwtUtil.getPhoneFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("无法获取JWT Token或JWT Token已过期: " + e.getMessage());
            }
        }
        
        // 验证token并设置认证信息
        if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwtToken)) {
                    // 构建用户详情
                    UserDetails userDetails = User.builder()
                            .username(phone)
                            .password("") // 密码不需要，因为已经通过JWT验证
                            .authorities(new ArrayList<>()) // 这里可以根据需要设置权限
                            .build();
                    
                    // 创建认证token
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("用户认证成功: " + phone);
                }
            } catch (Exception e) {
                logger.warn("JWT Token验证失败: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

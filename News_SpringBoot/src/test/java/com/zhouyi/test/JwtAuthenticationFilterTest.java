package com.zhouyi.test;

import com.zhouyi.common.filter.JwtAuthenticationFilter;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.entity.Role;
import com.zhouyi.entity.User;
import com.zhouyi.service.UserRoleService;
import com.zhouyi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JWT认证过滤器测试 - 验证权限修复
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1);
        testUser.setPhone("13800138000");
        testUser.setUsername("testuser");

        // 创建测试角色
        adminRole = new Role();
        adminRole.setId(1);
        adminRole.setRoleName("ADMIN");
        adminRole.setDescription("管理员角色");

        userRole = new Role();
        userRole.setId(2);
        userRole.setRoleName("USER");
        userRole.setDescription("普通用户角色");
    }

    @Test
    void testGetUserAuthorities_AdminUser() {
        // 模拟用户查询成功
        when(userService.getUserByPhone("13800138000"))
                .thenReturn(com.zhouyi.common.result.Result.success(testUser));

        // 模拟用户角色查询成功
        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(com.zhouyi.common.result.Result.success(List.of(adminRole)));

        // 使用反射调用私有方法进行测试
        try {
            var method = JwtAuthenticationFilter.class.getDeclaredMethod("getUserAuthorities", String.class);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>)
                    method.invoke(jwtAuthenticationFilter, "13800138000");

            // 验证结果
            assertNotNull(authorities);
            assertEquals(1, authorities.size());
            assertEquals("ROLE_ADMIN", authorities.get(0).getAuthority());

        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }

        // 验证服务调用
        verify(userService).getUserByPhone("13800138000");
        verify(userRoleService).getRolesDetailsByUserId(1);
    }

    @Test
    void testGetUserAuthorities_UserWithNoRoles() {
        // 模拟用户查询成功
        when(userService.getUserByPhone("13800138000"))
                .thenReturn(com.zhouyi.common.result.Result.success(testUser));

        // 模拟用户角色查询返回空列表
        when(userRoleService.getRolesDetailsByUserId(1))
                .thenReturn(com.zhouyi.common.result.Result.success(List.of()));

        try {
            var method = JwtAuthenticationFilter.class.getDeclaredMethod("getUserAuthorities", String.class);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>)
                    method.invoke(jwtAuthenticationFilter, "13800138000");

            // 验证结果 - 应该返回默认的USER角色
            assertNotNull(authorities);
            assertEquals(1, authorities.size());
            assertEquals("ROLE_USER", authorities.get(0).getAuthority());

        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    void testGetUserAuthorities_UserNotFound() {
        // 模拟用户查询失败
        when(userService.getUserByPhone("13800138000"))
                .thenReturn(com.zhouyi.common.result.Result.error("用户不存在"));

        try {
            var method = JwtAuthenticationFilter.class.getDeclaredMethod("getUserAuthorities", String.class);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>)
                    method.invoke(jwtAuthenticationFilter, "13800138000");

            // 验证结果 - 用户不存在时返回空权限列表
            assertNotNull(authorities);
            assertEquals(0, authorities.size());

        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }
}

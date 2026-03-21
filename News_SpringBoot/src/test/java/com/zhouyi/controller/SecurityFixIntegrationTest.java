package com.zhouyi.controller;

import com.zhouyi.common.security.CustomUserDetails;
import com.zhouyi.entity.User;
import com.zhouyi.mapper.UserMapper;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.component.EsSyncStartupRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试：验证安全修复效果（IDOR 漏洞修复）
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/schema-security-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitytestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=never",
        "spring.data.elasticsearch.enabled=false"
})
public class SecurityFixIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @MockitoBean
    private HeadlineEsRepository headlineEsRepository;

    @MockitoBean
    private EsSyncStartupRunner esSyncStartupRunner;

    private Integer userId1;
    private Integer userId2;
    private CustomUserDetails userDetails1;
    private CustomUserDetails adminDetails;

    @BeforeEach
    void setUp() {
        // 清理并准备测试数据
        userMapper.selectAllUsers().forEach(u -> userMapper.deleteUserById(u.getId()));

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPhone("13111111111");
        user1.setPassword("hashed_password");
        user1.setStatus(1);
        userMapper.insertUser(user1);
        userId1 = user1.getId();

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPhone("13222222222");
        user2.setPassword("hashed_password");
        user2.setStatus(1);
        userMapper.insertUser(user2);
        userId2 = user2.getId();

        // 准备 SecurityContext 用户详情
        userDetails1 = new CustomUserDetails(userId1, user1.getUsername(), user1.getPassword(), 
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        adminDetails = new CustomUserDetails(999, "admin", "admin_pass", 
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    }

    @Test
    void testUserCanAccessOwnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId1)
                .with(user(userDetails1)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserCannotAccessOtherProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId2)
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权限访问"));
    }

    @Test
    void testAdminCanAccessAnyProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId1)
                .with(user(adminDetails)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/" + userId2)
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }
}

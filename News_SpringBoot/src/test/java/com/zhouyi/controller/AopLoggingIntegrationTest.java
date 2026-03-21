package com.zhouyi.controller;

import com.zhouyi.common.security.CustomUserDetails;
import com.zhouyi.mapper.OperationLogMapper;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.component.EsSyncStartupRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试：验证 AOP 操作日志自动记录功能
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/schema-security-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:aoptestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=never",
        "spring.data.elasticsearch.enabled=false"
})
public class AopLoggingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @MockitoBean
    private StringRedisTemplate redisTemplate; // 模拟 Redis 避免限流切面报错

    @MockitoBean
    private HeadlineEsRepository headlineEsRepository;

    @MockitoBean
    private EsSyncStartupRunner esSyncStartupRunner;

    @Test
    void testAdminOperationIsLoggedAutomatically() throws Exception {
        // 1. 准备管理员身份
        CustomUserDetails adminDetails = new CustomUserDetails(1, "admin", "password", 
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

        // 2. 执行被 @LogOperation 标记的方法 (获取系统配置)
        mockMvc.perform(get("/api/v1/admin/config")
                .param("category", "system")
                .with(user(adminDetails)))
                .andExpect(status().isOk());

        // 3. 验证数据库中是否产生了日志记录
        var logs = operationLogMapper.selectByUserId(1, 10);
        assertTrue(logs.size() > 0, "应该自动产生至少一条操作日志");
        
        var latestLog = logs.get(0);
        assertTrue(latestLog.getOperationType().contains("READ"), "操作类型应包含 READ");
        assertTrue(latestLog.getResourceType().contains("CONFIG"), "资源类型应包含 CONFIG");
        assertTrue(latestLog.getDescription().contains("获取系统配置"), "描述应包含 '获取系统配置'");
    }
}

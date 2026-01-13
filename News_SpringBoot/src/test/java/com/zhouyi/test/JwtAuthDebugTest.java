package com.zhouyi.test;

import com.zhouyi.NewsSpringBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * JWT权限调试测试
 */
@SpringBootTest(classes = NewsSpringBootApplication.class)
@ActiveProfiles("test")
public class JwtAuthDebugTest {

    @Test
    public void testContextLoad() {
        // 测试Spring上下文加载
        System.out.println("Spring上下文加载成功");
    }
}

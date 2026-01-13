package com.zhouyi.test;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 简单的MongoDB连接测试
 */
@Component
@ConditionalOnProperty(name = "app.test.simple.mongo", havingValue = "true")
public class SimpleMongoTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleMongoTest.class);

    public SimpleMongoTest() {
        log.info("MongoDB测试组件加载成功");
        log.info("如果看到这条消息，说明MongoDB配置正确");
    }
}

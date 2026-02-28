package com.zhouyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 新闻头条项目主启动类
 * 
 * 基于 Spring Boot + MyBatis 开发的新闻管理系统
 * 前端对接 Vue + Element UI
 * 
 * @author News Team
 * @version 2.2
 */
@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class NewsSpringBootApplication {

    static void main(String[] args) {
        SpringApplication.run(NewsSpringBootApplication.class, args);
    }
}

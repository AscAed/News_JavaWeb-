package com.zhouyi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解 - 基于 Redis 实现分布式限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流 key 的前缀 (用于区分不同的业务接口)
     */
    String key() default "rate_limit:";

    /**
     * 限流次数
     */
    int count() default 10;

    /**
     * 时间范围 (秒)
     */
    int period() default 60;

    /**
     * 限流类型 (默认按 IP 限流)
     */
    LimitType limitType() default LimitType.IP;

    enum LimitType {
        /**
         * 默认策略：按客户端 IP 限流
         */
        IP,
        /**
         * 按用户名/用户 ID 限流 (需已登录)
         */
        USER,
        /**
         * 全局限流 (所有访问公用一个限制)
         */
        GLOBAL
    }
}

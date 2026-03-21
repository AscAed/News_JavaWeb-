package com.zhouyi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解 - 用于AOP自动记录审计日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    
    /**
     * 操作类型 (如: READ, CREATE, UPDATE, DELETE)
     */
    String operationType() default "";
    
    /**
     * 资源类型 (如: USER, HEADLINE, CONFIG)
     */
    String resourceType() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否包含请求参数 (默认为true)
     */
    boolean includeArgs() default true;
    
    /**
     * 是否包含返回值 (默认为false，防止大数据对象占用系统资源)
     */
    boolean includeResult() default false;
}

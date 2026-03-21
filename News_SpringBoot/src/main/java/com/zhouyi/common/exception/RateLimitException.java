package com.zhouyi.common.exception;

/**
 * 限流异常 - 当接口访问超过限制时抛出
 */
public class RateLimitException extends GlobalException {
    
    public RateLimitException(String message) {
        super(429, message);
    }
}

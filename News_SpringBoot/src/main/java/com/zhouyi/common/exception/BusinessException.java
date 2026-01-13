package com.zhouyi.common.exception;

/**
 * 业务异常类
 * 继承全局异常基类，提供业务相关的异常处理
 */
public class BusinessException extends GlobalException {

    public BusinessException(String message) {
        super(500, message);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
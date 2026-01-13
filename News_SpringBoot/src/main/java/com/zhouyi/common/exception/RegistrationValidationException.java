package com.zhouyi.common.exception;

/**
 * 注册校验异常类
 * 专门用于处理注册功能中的参数校验异常
 */
public class RegistrationValidationException extends GlobalException {

    public RegistrationValidationException(String message) {
        super(400, "注册参数校验失败: " + message);
    }

    public RegistrationValidationException(Integer code, String message) {
        super(code, "注册参数校验失败: " + message);
    }

    public RegistrationValidationException(String field, String message) {
        super(400, String.format("注册参数校验失败 - %s: %s", field, message));
    }

    public RegistrationValidationException(Integer code, String message, Throwable cause) {
        super(code, "注册参数校验失败: " + message, cause);
    }
}

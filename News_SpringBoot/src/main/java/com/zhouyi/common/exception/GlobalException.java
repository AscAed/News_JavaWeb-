package com.zhouyi.common.exception;

/**
 * 全局异常基类
 * 提供统一的异常处理基础结构
 */
public abstract class GlobalException extends RuntimeException {

    private Integer code;
    private String message;

    public GlobalException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public GlobalException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

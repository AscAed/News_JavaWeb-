package com.zhouyi.common.exception;

import com.zhouyi.common.result.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends GlobalException {

    private ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getCode(), resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getCode(), resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }

    public BusinessException(String message) {
        super(500, message);
    }
}
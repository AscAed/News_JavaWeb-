package com.zhouyi.common.exception;

import com.zhouyi.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常 BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        logger.warn("业务代码异常: code={}, message={}", e.getCode(), e.getMessage());
        if (e.getResultCode() != null) {
            return Result.error(e.getResultCode());
        }
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 注册校验异常处理
     */
    @ExceptionHandler(RegistrationValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleRegistrationValidationException(RegistrationValidationException e) {
        logger.warn("注册校验异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理 (用于@RequestBody校验)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("参数校验失败: {}", message);
        return Result.error(400, "参数校验失败: " + message);
    }

    /**
     * 参数绑定异常处理 (用于表单数据校验)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("参数绑定失败: {}", message);
        return Result.error(400, "参数绑定失败: " + message);
    }

    /**
     * 路径参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Class<?> requiredType = e.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "未知";
        String message = String.format("参数 '%s' 类型不匹配，期望类型: %s",
                e.getName(), typeName);
        logger.warn("参数类型不匹配: {}", message);
        return Result.error(400, message);
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("缺少必需的请求参数: %s (类型: %s)",
                e.getParameterName(), e.getParameterType());
        logger.warn("缺少请求参数: {}", message);
        return Result.error(400, message);
    }

    /**
     * 不支持的HTTP方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = String.format("不支持的HTTP方法: %s，支持的方法: %s",
                e.getMethod(), e.getSupportedMethods() != null ? String.join(", ", e.getSupportedMethods()) : "无");
        logger.warn("不支持的HTTP方法: {}", message);
        return Result.error(405, message);
    }

    /**
     * 不支持的媒体类型异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        String message = String.format("不支持的媒体类型: %s，支持的类型: %s",
                e.getContentType(), e.getSupportedMediaTypes());
        logger.warn("不支持的媒体类型: {}", message);
        return Result.error(415, message);
    }

    /**
     * 请求消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = "请求体格式错误或无法解析";
        logger.warn("请求消息不可读: {}", e.getMessage());
        return Result.error(400, message);
    }

    /**
     * 404异常 - 资源不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<String> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String message = String.format("请求的资源不存在: %s %s", e.getHttpMethod(), e.getRequestURL());
        logger.warn("资源不存在: {}", message);
        return Result.error(404, message);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数异常: {}", e.getMessage());
        return Result.error(400, "非法参数: " + e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return Result.error(500, "系统内部错误：空指针异常");
    }

    /**
     * 非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleIllegalStateException(IllegalStateException e) {
        logger.error("非法状态异常", e);
        return Result.error(500, "系统状态异常: " + e.getMessage());
    }

    /**
     * 数据库异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleSQLException(SQLException e) {
        logger.error("数据库底层异常", e);
        return Result.error(500, "数据库操作失败");
    }

    /**
     * 数据完整性校验异常 (如：唯一索引冲突)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String msg = e.getMessage();
        logger.warn("数据完整性约束违反: {}", msg);

        if (msg != null) {
            String lowerMsg = msg.toLowerCase();
            boolean isDuplicate = lowerMsg.contains("duplicate entry") || lowerMsg.contains("unique index");

            // 针对手机号重复的特殊处理 (MySQL/H2)
            if (isDuplicate && (lowerMsg.contains("users.phone") || lowerMsg.contains("users(phone)"))) {
                return Result.error(409, "添加失败：该手机号已注册");
            }

            // 通用唯一索引冲突处理
            if (isDuplicate) {
                return Result.error(409, "数据已存在，请勿重复添加");
            }
        }

        return Result.error(409, "数据操作失败：违反唯一性约束");
    }

    /**
     * 访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        logger.warn("访问拒绝: {}", e.getMessage());
        return Result.error(403, "无权限访问");
    }

    /**
     * 约束违反异常 (用于方法参数校验@PathVariable等)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        logger.warn("约束违反: {}", message);
        return Result.error(400, "参数校验失败: " + message);
    }

    /**
     * 处理自定义全局异常
     */
    @ExceptionHandler(GlobalException.class)
    public Result<String> handleGlobalException(GlobalException e) {
        logger.warn("业务/全局异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        logger.error("未处理的系统异常", e);
        // 不向用户暴露具体的异常消息，保护系统安全
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}

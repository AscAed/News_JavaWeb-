package com.zhouyi.common.result;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * 统一响应结果封装类
 */
public class Result<T> {
    private int code; // 状态码：200-成功，400-客户端错误，500-服务器错误等
    private String message; // 响应消息
    private T data; // 响应数据（泛型，支持任意类型）
    private String timestamp; // 响应时间戳（ISO 8601格式）
    private String path; // 请求路径（可选）

    // 私有构造函数，强制使用静态方法构建
    private Result(int code, String message, T data, String path) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        this.path = path;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, null);
    }

    // 成功响应（无数据，带路径）
    public static <T> Result<T> successWithPath(String path) {
        return new Result<>(200, "操作成功", null, path);
    }

    // 成功响应（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, null);
    }

    // 成功响应（有数据，带路径）
    public static <T> Result<T> success(T data, String path) {
        return new Result<>(200, "操作成功", data, path);
    }

    // 成功响应（自定义消息，无数据）
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message, null, null);
    }

    // 成功响应（自定义消息，无数据，带路径）
    public static <T> Result<T> success(String message, String path) {
        return new Result<>(200, message, null, path);
    }

    // 成功响应（自定义消息和数据）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, null);
    }

    // 成功响应（自定义消息和数据，带路径）
    public static <T> Result<T> success(String message, T data, String path) {
        return new Result<>(200, message, data, path);
    }

    // 201 Created 响应
    public static <T> Result<T> created(String message, T data, String path) {
        return new Result<>(201, message, data, path);
    }

    // 失败响应（默认消息）
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null, null);
    }

    // 失败响应（默认消息，带路径）
    public static <T> Result<T> errorWithPath(String path) {
        return new Result<>(500, "操作失败", null, path);
    }

    // 失败响应（自定义消息）
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, null);
    }

    // 失败响应（自定义消息，带路径）
    public static <T> Result<T> error(String message, String path) {
        return new Result<>(500, message, null, path);
    }

    // 失败响应（自定义状态码和消息）
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, null);
    }

    // 失败响应（自定义状态码和消息，带路径）
    public static <T> Result<T> error(int code, String message, String path) {
        return new Result<>(code, message, null, path);
    }

    // 失败响应（自定义状态码，消息和数据）
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data, null);
    }

    // Getter方法
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    // Setter方法（主要用于测试）
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 判断是否成功
    public boolean isSuccess() {
        return this.code == 200;
    }

    // 判断是否客户端错误
    public boolean isClientError() {
        return this.code >= 400 && this.code < 500;
    }

    // 判断是否服务器错误
    public boolean isServerError() {
        return this.code >= 500;
    }
}

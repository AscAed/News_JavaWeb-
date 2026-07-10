package com.zhouyi.common.result;

import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(200, "操作成功"),

    /* 参数错误：40001-40999 */
    PARAM_IS_INVALID(40001, "参数无效"),
    PARAM_IS_BLANK(40002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(40003, "参数类型错误"),
    PARAM_NOT_COMPLETE(40004, "参数缺失"),

    /* 用户错误：20001-29999 */
    USER_NOT_LOGGED_IN(20001, "用户未登录，请先登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),

    /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(30001, "业务单位不存在"),
    ARTICLE_NOT_FOUND(30002, "新闻文章不存在"),

    /* 系统错误：50001-59999 */
    SYSTEM_INNER_ERROR(50001, "系统内部繁忙，请稍后再试"),

    /* 数据错误：60001-69999 */
    RESULE_DATA_NONE(60001, "数据未找到"),
    DATA_IS_WRONG(60002, "数据有误"),
    DATA_ALREADY_EXISTED(60003, "数据已存在"),

    /* 接口错误：70001-79999 */
    INTERFACE_EXCEED_LOAD(70001, "接口负载过高"),

    /* 权限错误：80001-89999 */
    PERMISSION_NO_ACCESS(80001, "无访问权限");

    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

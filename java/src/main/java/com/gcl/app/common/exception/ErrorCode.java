// file: src/main/java/com/gcl/app/common/exception/ErrorCode.java
package com.gcl.app.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码枚举
 * 码段设计：
 * 0           — 成功
 * 40001-40099 — 参数校验错误
 * 40100-40199 — 认证错误
 * 40300-40399 — 权限错误
 * 40400-40499 — 资源不存在
 * 40900-40999 — 冲突
 * 50000-50099 — 系统内部错误
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 成功
    SUCCESS(0, "success", 200),

    // 参数校验错误 40001-40099
    VALIDATION_ERROR(40001, "参数校验失败", 400),
    PARAM_MISSING(40002, "必填参数缺失", 400),

    // 认证错误 40100-40199
    UNAUTHORIZED(40100, "未登录或Token已过期", 401),
    LOGIN_FAILED(40101, "用户名或密码错误", 401),
    TOKEN_EXPIRED(40102, "Token已过期", 401),
    TOKEN_INVALID(40103, "Token无效", 401),

    // 权限错误 40300-40399
    FORBIDDEN(40300, "无权访问该资源", 403),
    PERMISSION_DENIED(40301, "权限不足", 403),

    // 资源不存在 40400-40499
    NOT_FOUND(40400, "资源不存在", 404),
    USER_NOT_FOUND(40401, "用户不存在", 404),
    ROLE_NOT_FOUND(40402, "角色不存在", 404),
    PERMISSION_NOT_FOUND(40403, "权限不存在", 404),
    MENU_NOT_FOUND(40404, "菜单不存在", 404),

    // 冲突 40900-40999
    CONFLICT(40900, "资源冲突", 409),
    USER_EXISTS(40901, "用户名已存在", 409),
    ROLE_EXISTS(40902, "角色名已存在", 409),
    PERMISSION_EXISTS(40903, "权限编码已存在", 409),

    // 系统内部错误 50000-50099
    INTERNAL_ERROR(50000, "系统内部错误", 500),
    DB_ERROR(50001, "数据库异常", 500);

    private final int code;
    private final String message;
    private final int httpStatus;
}

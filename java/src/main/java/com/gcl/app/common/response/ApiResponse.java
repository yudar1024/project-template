// file: src/main/java/com/gcl/app/common/response/ApiResponse.java
package com.gcl.app.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.common.util.TraceIdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一 API 响应封装
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private String timestamp;
    private String traceId;

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getMessage());
        response.setData(data);
        response.setTimestamp(LocalDateTime.now().toString());
        response.setTraceId(TraceIdUtil.getTraceId());
        return response;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = success(data);
        response.setMessage(message);
        return response;
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return fail(errorCode, errorCode.getMessage());
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now().toString());
        response.setTraceId(TraceIdUtil.getTraceId());
        return response;
    }

    /**
     * 失败响应（携带错误详情，如校验错误列表）
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message, T data) {
        ApiResponse<T> response = fail(errorCode, message);
        response.setData(data);
        return response;
    }
}

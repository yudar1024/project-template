// file: src/main/java/com/gcl/app/common/exception/BusinessException.java
package com.gcl.app.common.exception;

/**
 * 业务异常 — 用于应用层抛出的可预期业务错误
 */
public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

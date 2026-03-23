// file: src/main/java/com/gcl/app/common/util/TraceIdUtil.java
package com.gcl.app.common.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * 链路追踪 ID 工具类
 */
public final class TraceIdUtil {

    public static final String TRACE_ID_KEY = "traceId";

    private TraceIdUtil() {
        // 工具类禁止实例化
    }

    /**
     * 生成并设置 traceId 到 MDC
     */
    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    /**
     * 从 MDC 获取当前 traceId
     */
    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        return traceId != null ? traceId : "";
    }

    /**
     * 清除 MDC 中的 traceId
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }
}

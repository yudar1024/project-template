// file: src/main/java/com/gcl/app/interfaces/filter/TraceIdFilter.java
package com.gcl.app.interfaces.filter;

import com.gcl.app.common.util.TraceIdUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * TraceId 过滤器 — 为每个请求生成链路追踪 ID
 */
@Component
@WebFilter("/*")
@Order(1)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String traceId = TraceIdUtil.generateTraceId();
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader("X-Trace-Id", traceId);
            }
            chain.doFilter(request, response);
        } finally {
            TraceIdUtil.clearTraceId();
        }
    }
}

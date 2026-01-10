package com.luckin.javademo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.StringJoiner;

/**
 * 最小关键链路日志：
 * - 记录入参摘要（仅字段名/数量，不记录值，避免泄漏敏感信息）
 * - 记录耗时、状态码，便于快速定位慢请求与错误请求
 * - traceId 由 TraceIdFilter 写入 MDC，日志格式中即可输出
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        long startNs = System.nanoTime();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String querySummary = summarizeQueryParamNames(request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            int status = response.getStatus();
            String traceId = currentTraceId();

            if (status >= 500) {
                log.error("request finished, traceId={}, method={}, path={}, status={}, durationMs={}, queryParams={}",
                        traceId, method, path, status, durationMs, querySummary);
            } else if (status >= 400) {
                log.warn("request finished, traceId={}, method={}, path={}, status={}, durationMs={}, queryParams={}",
                        traceId, method, path, status, durationMs, querySummary);
            } else {
                log.info("request finished, traceId={}, method={}, path={}, status={}, durationMs={}, queryParams={}",
                        traceId, method, path, status, durationMs, querySummary);
            }
        }
    }

    private String summarizeQueryParamNames(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        if (names == null || !names.hasMoreElements()) {
            return "[]";
        }
        StringJoiner sj = new StringJoiner(",", "[", "]");
        int count = 0;
        while (names.hasMoreElements() && count < 20) {
            sj.add(names.nextElement());
            count++;
        }
        if (names.hasMoreElements()) {
            sj.add("...more");
        }
        return sj.toString();
    }

    private String currentTraceId() {
        String traceId = MDC.get(TraceIdFilter.MDC_KEY_TRACE_ID);
        return (traceId == null || traceId.isBlank()) ? "-" : traceId;
    }
}


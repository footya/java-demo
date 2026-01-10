package com.luckin.javademo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * traceId 过滤器：
 * - 从请求头读取 `X-Request-Id`，若缺失则生成
 * - 写入 MDC，确保后续日志自动带上 traceId
 * - 在响应头回传 `X-Request-Id`，便于客户端与日志关联
 *
 * 安全与边界：
 * - 对传入的 request id 做格式与长度约束，避免日志注入与过长字段污染日志
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String MDC_KEY_TRACE_ID = "traceId";

    private static final int MAX_TRACE_ID_LEN = 64;
    private static final Pattern ALLOWED = Pattern.compile("^[A-Za-z0-9._-]{1,64}$");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String incoming = request.getHeader(HEADER_REQUEST_ID);
        String traceId = normalizeOrNew(incoming);

        MDC.put(MDC_KEY_TRACE_ID, traceId);
        response.setHeader(HEADER_REQUEST_ID, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY_TRACE_ID);
        }
    }

    private String normalizeOrNew(String incoming) {
        if (incoming == null) {
            return newTraceId();
        }
        String trimmed = incoming.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_TRACE_ID_LEN) {
            return newTraceId();
        }
        if (!ALLOWED.matcher(trimmed).matches()) {
            return newTraceId();
        }
        return trimmed;
    }

    private String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}


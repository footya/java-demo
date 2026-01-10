package com.luckin.javademo.error;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 标准错误响应：
 * - 作为所有错误场景的统一输出结构（JSON）
 * - traceId 用于定位日志；timestamp 用于排查时序；path 用于定位接口
 */
public record ApiErrorResponse(
        String code,
        String message,
        String traceId,
        String path,
        OffsetDateTime timestamp,
        List<ApiFieldError> errors
) {}


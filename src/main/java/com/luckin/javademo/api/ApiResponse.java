package com.luckin.javademo.api;

/**
 * 统一成功响应外层结构：
 * - 目的：让调用方用同一套解析逻辑处理不同接口的成功返回
 * - 与错误响应（ApiErrorResponse）区分：错误响应由全局异常处理统一返回
 *
 * 字段约定：
 * - code：成功码，当前统一为 OK
 * - message：对人可读的提示文本（成功时通常为 OK）
 * - traceId：链路标识，用于把一次请求的客户端响应与服务端日志关联起来
 * - data：具体业务数据；允许为 null
 */
public record ApiResponse<T>(
        String code,
        String message,
        String traceId,
        T data
) {
}


package com.luckin.javademo.error;

/**
 * 对外稳定的错误码集合：
 * - 与 HTTP status 解耦：status 表达“协议层语义”，code 表达“业务/错误类型语义”
 * - 便于客户端做稳定分支处理与告警归类（不依赖 message 文本）
 */
public enum ErrorCode {
    /**
     * 通用请求不合法：参数缺失、格式不对、业务规则不满足但仍归类为 400 等。
     */
    INVALID_REQUEST,
    /**
     * 入参校验失败：Bean Validation / 绑定校验等，可返回字段级错误数组。
     */
    VALIDATION_FAILED,
    /**
     * 媒体类型不支持：如接口只接受 JSON，但客户端传了非 JSON。
     */
    UNSUPPORTED_MEDIA_TYPE,
    /**
     * 未知/未捕获异常：统一归类为 500，避免泄漏内部细节。
     */
    INTERNAL_ERROR
}


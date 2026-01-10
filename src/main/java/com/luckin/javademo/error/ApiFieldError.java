package com.luckin.javademo.error;

/**
 * 字段级错误信息：
 * - 用于参数校验失败（400）时回传，让调用方能定位到具体字段
 * - message 面向调用方，不应包含敏感信息或内部实现细节
 */
public record ApiFieldError(String field, String message) {}


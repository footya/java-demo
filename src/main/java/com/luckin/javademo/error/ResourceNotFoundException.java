package com.luckin.javademo.error;

/**
 * 资源不存在异常：
 * - Service 层用于表达“业务上找不到目标资源”，避免把 HTTP/状态码语义下沉到业务层
 * - 由 GlobalExceptionHandler 统一映射为 404 + 标准错误响应
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(resourceName + " not found");
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getResourceId() {
        return resourceId;
    }
}


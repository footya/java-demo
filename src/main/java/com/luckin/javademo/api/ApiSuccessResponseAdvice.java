package com.luckin.javademo.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckin.javademo.error.ApiErrorResponse;
import com.luckin.javademo.logging.TraceIdFilter;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 成功响应统一外层封装：
 * - Controller 只关注协议层入参/路由与调用 Service，避免每个接口重复拼装 code/message/traceId
 * - 仅对 2xx 响应生效；错误响应由 GlobalExceptionHandler 统一输出 ApiErrorResponse
 *
 * 边界说明：
 * - 避免二次封装：若返回值已是 ApiResponse 或 ApiErrorResponse，则直接透传
 * - String 返回值需要特殊处理：StringHttpMessageConverter 期望 body 是 String
 */
@RestControllerAdvice
public class ApiSuccessResponseAdvice implements ResponseBodyAdvice<Object> {
    private static final String SUCCESS_CODE = "OK";
    private static final String SUCCESS_MESSAGE = "OK";

    private final ObjectMapper objectMapper;

    public ApiSuccessResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (!is2xx(response)) {
            return body;
        }

        if (body instanceof ApiResponse<?> || body instanceof ApiErrorResponse) {
            return body;
        }

        ApiResponse<Object> wrapped = new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, currentTraceId(), body);

        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("成功响应序列化失败", e);
            }
        }

        return wrapped;
    }

    private boolean is2xx(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servlet) {
            int status = servlet.getServletResponse().getStatus();
            if (status <= 0) {
                return true;
            }
            return status >= 200 && status < 300;
        }
        return true;
    }

    private String currentTraceId() {
        String traceId = MDC.get(TraceIdFilter.MDC_KEY_TRACE_ID);
        return (traceId == null || traceId.isBlank()) ? "-" : traceId;
    }
}


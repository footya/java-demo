package com.luckin.javademo.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 全局异常处理：
 * - Controller 专注协议层与编排调用；异常到这里统一转换为标准错误响应
 * - 安全：对外不回传堆栈；对内日志可打印堆栈（仅 5xx）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Bean Validation on @RequestBody 的校验失败（如 @NotBlank）。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                         HttpServletRequest request) {
        List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toApiFieldError)
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "参数校验失败", request, errors);
    }

    /**
     * 绑定/校验失败（如 query 参数、表单参数绑定失败）。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toApiFieldError)
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "参数校验失败", request, errors);
    }

    /**
     * 请求体 JSON 解析失败/缺失等（如空 body、格式错误）。
     * - 统一归类为 400，避免把底层解析细节暴露给调用方
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                              HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, "请求体不可解析", request, null);
    }

    /**
     * 缺少必须的请求参数（如 @RequestParam(required=true)）。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                               HttpServletRequest request) {
        String message = "缺少参数: " + ex.getParameterName();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, message, request, null);
    }

    /**
     * 参数类型不匹配（如 city=123 传到 String 还好，但 int/enum 等会出现类型不匹配）。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        String message = "参数类型不匹配: " + ex.getName();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, message, request, null);
    }

    /**
     * Content-Type 不支持（如接口只接受 JSON）。
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                        HttpServletRequest request) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA_TYPE, "不支持的媒体类型", request, null);
    }

    /**
     * Spring 6 / Boot 3 的一类标准化异常基类（包含 status 与 body 信息）。
     * - 例如：HttpRequestMethodNotSupportedException 等
     * - 这里做兜底映射：4xx -> INVALID_REQUEST；5xx -> INTERNAL_ERROR
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleErrorResponseException(ErrorResponseException ex,
                                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorCode code = status.is4xxClientError() ? ErrorCode.INVALID_REQUEST : ErrorCode.INTERNAL_ERROR;
        String detail = (ex.getBody() == null) ? null : ex.getBody().getDetail();
        String message = status.is5xxServerError() ? "系统繁忙，请稍后再试" : safeMessage(detail, "请求不合法");
        if (status.is5xxServerError()) {
            log.error("server error, traceId={}, method={}, path={}, status={}",
                    currentTraceId(), request.getMethod(), request.getRequestURI(), status.value(), ex);
        }
        return build(status, code, message, request, null);
    }

    /**
     * 显式抛出的带状态码异常（当前工程已有使用）。
     * - 4xx -> INVALID_REQUEST（除 415 已被上面单独处理）
     * - 5xx -> INTERNAL_ERROR（对外统一提示）
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        if (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
            return build(status, ErrorCode.UNSUPPORTED_MEDIA_TYPE, "不支持的媒体类型", request, null);
        }
        if (status.is5xxServerError()) {
            log.error("server error, traceId={}, method={}, path={}, status={}",
                    currentTraceId(), request.getMethod(), request.getRequestURI(), status.value(), ex);
            return build(status, ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后再试", request, null);
        }
        String message = safeMessage(ex.getReason(), "请求不合法");
        return build(status, ErrorCode.INVALID_REQUEST, message, request, null);
    }

    /**
     * 静态资源未找到等（避免返回默认 HTML/JSON 结构不一致）。
     * - 该异常是否出现取决于 Spring MVC 的配置与资源映射
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                                  HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "资源不存在", request, null);
    }

    /**
     * 业务资源不存在（CRUD 典型场景：按 id 查询/更新/删除找不到记录）。
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "资源不存在", request, null);
    }

    /**
     * 兜底异常：对外统一 INTERNAL_ERROR；对内记录堆栈便于排障。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("unexpected error, traceId={}, method={}, path={}",
                currentTraceId(), request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后再试", request, null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status,
                                                   ErrorCode code,
                                                   String message,
                                                   HttpServletRequest request,
                                                   List<ApiFieldError> errors) {
        ApiErrorResponse body = new ApiErrorResponse(
                code.name(),
                message,
                currentTraceId(),
                request.getRequestURI(),
                OffsetDateTime.now(),
                errors
        );
        return ResponseEntity.status(status.value()).body(body);
    }

    private ApiFieldError toApiFieldError(FieldError fe) {
        String field = fe.getField();
        String message = safeMessage(fe.getDefaultMessage(), "参数不合法");
        return new ApiFieldError(field, message);
    }

    /**
     * 对外 message 的最小安全策略：
     * - 只允许输出我们自己提供的短文本（如 “city required” / “参数类型不匹配”）
     * - 不把异常的堆栈、类名、SQL、URL、token 等内部信息拼进 message
     */
    private String safeMessage(String message, String fallback) {
        if (message == null || message.isBlank()) {
            return fallback;
        }
        String trimmed = message.trim();
        if (trimmed.length() > 200) {
            return fallback;
        }
        return trimmed;
    }

    private String currentTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId == null || traceId.isBlank()) ? "-" : traceId;
    }
}


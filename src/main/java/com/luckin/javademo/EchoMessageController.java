package com.luckin.javademo;

import com.luckin.javademo.service.EchoMessageCrudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * EchoMessage CRUD 接口（协议层）：
 * - 只做路由、入参解析/校验与调用 Service
 * - 成功响应的统一外层结构由 ApiSuccessResponseAdvice 自动封装
 */
@RestController
public class EchoMessageController {
    private final EchoMessageCrudService echoMessageCrudService;

    public EchoMessageController(EchoMessageCrudService echoMessageCrudService) {
        this.echoMessageCrudService = echoMessageCrudService;
    }

    @PostMapping(value = "/echo-messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EchoMessageResponse create(@Valid @RequestBody CreateEchoMessageRequest request) {
        EchoMessageCrudService.EchoMessageResult result = echoMessageCrudService.create(request.message());
        return toResponse(result);
    }

    @GetMapping(value = "/echo-messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EchoMessageResponse getById(@PathVariable("id") @Positive(message = "id must be positive") long id) {
        EchoMessageCrudService.EchoMessageResult result = echoMessageCrudService.getById(id);
        return toResponse(result);
    }

    @PutMapping(value = "/echo-messages/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EchoMessageResponse updateById(@PathVariable("id") @Positive(message = "id must be positive") long id,
                                         @Valid @RequestBody UpdateEchoMessageRequest request) {
        EchoMessageCrudService.EchoMessageResult result = echoMessageCrudService.updateById(id, request.message());
        return toResponse(result);
    }

    @DeleteMapping(value = "/echo-messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteById(@PathVariable("id") @Positive(message = "id must be positive") long id) {
        echoMessageCrudService.deleteById(id);
        return Map.of("deleted", true);
    }

    private EchoMessageResponse toResponse(EchoMessageCrudService.EchoMessageResult result) {
        return new EchoMessageResponse(result.id(), result.message(), result.length(), result.createdAt());
    }

    /**
     * Create 请求体 DTO：
     * - message 必须非空且非全空白
     */
    public record CreateEchoMessageRequest(@NotBlank(message = "message required") String message) {
    }

    /**
     * Update 请求体 DTO：
     * - 目前只支持更新 message
     */
    public record UpdateEchoMessageRequest(@NotBlank(message = "message required") String message) {
    }

    /**
     * 响应体 DTO（业务数据部分）：
     * - 外层统一结构由 ApiSuccessResponseAdvice 自动补齐（code/message/traceId/data）
     */
    public record EchoMessageResponse(Long id, String message, Integer length, Instant createdAt) {
    }
}


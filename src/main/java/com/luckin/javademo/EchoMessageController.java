package com.luckin.javademo;

import com.luckin.javademo.service.EchoMessageCrudService;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;
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

    /**
     * 列表查询接口（协议层）：
     * - 参数校验交给 Bean Validation（不通过则由 GlobalExceptionHandler 统一输出 400 + 标准错误响应）
     * - Controller 只做：入参归一化（默认值/白名单映射）-> 调用 Service -> 组装响应 DTO
     */
    @GetMapping(value = "/echo-messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public EchoMessageListData list(@Valid @ModelAttribute EchoMessageListQuery query) {
        EchoMessageCrudService.EchoMessageListResult result = echoMessageCrudService.list(
                new EchoMessageCrudService.EchoMessageListQuery(
                        query.page(),
                        query.size(),
                        parseOrder(query.order()),
                        mapSortField(query.sort()),
                        normalizeContains(query.message()),
                        query.createdAtFrom(),
                        query.createdAtTo()
                )
        );

        List<EchoMessageResponse> items = result.items().stream()
                .map(this::toResponse)
                .toList();

        PageMeta page = new PageMeta(
                result.page().page(),
                result.page().size(),
                result.page().totalElements(),
                result.page().totalPages()
        );

        return new EchoMessageListData(items, page);
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

    /**
     * 列表查询的 query 参数 DTO（协议层）：
     * - 使用 @ModelAttribute 绑定 query 参数（?page=1&size=20...）
     * - 通过 Bean Validation 做参数合法性校验
     *
     * 参数约定：
     * - page 为 1 基（便于调用方理解），缺省为 1
     * - size 缺省为 20，并限制最大值，避免一次性拉取过多导致内存/响应时间问题
     * - sort/order 采用白名单枚举字符串，避免把任意字段名透传到 ORM
     * - createdAtFrom/createdAtTo 使用 ISO 8601（如 2026-01-01T00:00:00Z）
     */
    public record EchoMessageListQuery(
            @Min(value = 1, message = "page must be >= 1")
            Integer page,

            @Min(value = 1, message = "size must be >= 1")
            @Max(value = 100, message = "size must be <= 100")
            Integer size,

            @Pattern(regexp = "^(id|createdAt|message|length)$", message = "sort must be one of: id, createdAt, message, length")
            String sort,

            @Pattern(regexp = "^(asc|desc)$", message = "order must be asc or desc")
            String order,

            String message,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant createdAtFrom,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant createdAtTo
    ) {
        public EchoMessageListQuery {
            if (page == null) {
                page = 1;
            }
            if (size == null) {
                size = 20;
            }
            if (sort == null || sort.isBlank()) {
                sort = "createdAt";
            }
            if (order == null || order.isBlank()) {
                order = "desc";
            }
        }

        @AssertTrue(message = "createdAtFrom must be <= createdAtTo")
        public boolean isCreatedAtRangeValid() {
            if (createdAtFrom == null || createdAtTo == null) {
                return true;
            }
            return !createdAtFrom.isAfter(createdAtTo);
        }
    }

    /**
     * 列表查询的 data 部分：
     * - 统一成功响应外层由 ApiSuccessResponseAdvice 封装
     * - 这里仅定义 data.items 与 data.page 的结构
     */
    public record EchoMessageListData(List<EchoMessageResponse> items, PageMeta page) {
    }

    /**
     * 分页元信息（协议层）：
     * - page 为 1 基
     */
    public record PageMeta(int page, int size, long totalElements, int totalPages) {
    }

    private Sort.Direction parseOrder(String order) {
        if ("asc".equalsIgnoreCase(order)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "id" -> "id";
            case "createdAt" -> "createdAt";
            case "message" -> "message";
            case "length" -> "length";
            default -> "createdAt";
        };
    }

    private String normalizeContains(String message) {
        if (message == null) {
            return null;
        }
        String trimmed = message.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


package com.luckin.javademo.service;

import com.luckin.javademo.error.ResourceNotFoundException;
import com.luckin.javademo.persistence.EchoMessageEntity;
import com.luckin.javademo.persistence.EchoMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * EchoMessage CRUD 业务服务：
 * - 只承载业务含义（创建/读取/更新/删除），不关心 HTTP/状态码/JSON 结构
 * - 资源不存在等业务状态，通过抛出业务异常表达，由全局异常处理映射为标准错误响应
 */
@Service
public class EchoMessageCrudService {
    private final EchoMessageRepository echoMessageRepository;

    public EchoMessageCrudService(EchoMessageRepository echoMessageRepository) {
        this.echoMessageRepository = echoMessageRepository;
    }

    public EchoMessageResult create(String message) {
        EchoMessageEntity saved = echoMessageRepository.saveAndFlush(new EchoMessageEntity(message, message.length()));
        Long savedId = saved.getId();
        if (savedId == null) {
            throw new IllegalStateException("创建 echo_message 失败：未生成主键");
        }
        EchoMessageEntity reloaded = echoMessageRepository.findById(savedId)
                .orElseThrow(() -> new ResourceNotFoundException("echo_message", savedId));
        return toResult(reloaded);
    }

    public EchoMessageResult getById(long id) {
        EchoMessageEntity entity = echoMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("echo_message", id));
        return toResult(entity);
    }

    public EchoMessageResult updateById(long id, String message) {
        EchoMessageEntity entity = echoMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("echo_message", id));
        entity.updateMessageAndLength(message, message.length());
        EchoMessageEntity saved = echoMessageRepository.save(entity);
        return toResult(saved);
    }

    public void deleteById(long id) {
        boolean exists = echoMessageRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("echo_message", id);
        }
        echoMessageRepository.deleteById(id);
    }

    /**
     * 列表查询：
     * - 支持分页（page/size），page 为 1 基
     * - 支持排序（sort/order），仅允许在白名单字段内排序，避免把任意字段名透传到 ORM
     * - 支持基础过滤：message 子串匹配；createdAt 的时间范围（含边界）
     */
    public EchoMessageListResult list(EchoMessageListQuery query) {
        String sortField = query.sortField();
        if (sortField == null || sortField.isBlank()) {
            sortField = "createdAt";
        }
        Sort sort = Sort.by(sortField);
        Sort.Direction order = query.order();
        sort = (order == Sort.Direction.ASC) ? sort.ascending() : sort.descending();
        PageRequest pageRequest = PageRequest.of(query.pageIndex0(), query.size(), sort);

        Specification<EchoMessageEntity> spec = Specification.where(null);
        if (query.messageContains() != null && !query.messageContains().isBlank()) {
            String keyword = query.messageContains().trim();
            spec = spec.and(messageContains(keyword));
        }
        if (query.createdAtFrom() != null) {
            spec = spec.and(createdAtGte(query.createdAtFrom()));
        }
        if (query.createdAtTo() != null) {
            spec = spec.and(createdAtLte(query.createdAtTo()));
        }

        Page<EchoMessageEntity> page = echoMessageRepository.findAll(spec, pageRequest);

        List<EchoMessageResult> items = page.getContent().stream()
                .map(this::toResult)
                .toList();

        EchoMessagePageMeta pageMeta = new EchoMessagePageMeta(
                query.page(),
                query.size(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new EchoMessageListResult(items, pageMeta);
    }

    private EchoMessageResult toResult(EchoMessageEntity entity) {
        Long id = entity.getId();
        if (id == null) {
            throw new IllegalStateException("echo_message 主键缺失");
        }
        return new EchoMessageResult(
                id,
                entity.getMessage(),
                entity.getLength(),
                entity.getCreatedAt()
        );
    }

    /**
     * Service 层的领域结果：
     * - 不复用 Controller 的响应 DTO，避免协议层模型渗透到业务层
     */
    public record EchoMessageResult(Long id, String message, Integer length, Instant createdAt) {
    }

    /**
     * 列表查询的入参（服务层视角）：
     * - 将 Controller 的协议参数（字符串/枚举等）归一化为 Service 可执行的查询参数
     * - sortField 是实体字段名白名单映射后的结果，防止注入与运行时字段不存在
     */
    public record EchoMessageListQuery(
            int page,
            int size,
            Sort.Direction order,
            String sortField,
            String messageContains,
            Instant createdAtFrom,
            Instant createdAtTo
    ) {
        public int pageIndex0() {
            return Math.max(0, page - 1);
        }
    }

    /**
     * 列表查询结果：
     * - items 为业务数据列表
     * - page 为分页元信息（用于 Controller 组装 data.page）
     */
    public record EchoMessageListResult(List<EchoMessageResult> items, EchoMessagePageMeta page) {
    }

    /**
     * 分页元信息（与框架 Page 不耦合，避免协议层直接暴露框架对象）。
     */
    public record EchoMessagePageMeta(int page, int size, long totalElements, int totalPages) {
    }

    private Specification<EchoMessageEntity> messageContains(String keyword) {
        return (root, cq, cb) -> cb.like(
                root.get("message"),
                "%" + escapeLike(keyword) + "%",
                '\\'
        );
    }

    private Specification<EchoMessageEntity> createdAtGte(Instant from) {
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    private Specification<EchoMessageEntity> createdAtLte(Instant to) {
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    /**
     * LIKE 关键字转义：
     * - 用户输入中若包含 '%' '_' '\'，需要转义，否则会被当作通配符/转义符影响匹配结果
     */
    private String escapeLike(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}


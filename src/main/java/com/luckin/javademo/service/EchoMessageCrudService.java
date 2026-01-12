package com.luckin.javademo.service;

import com.luckin.javademo.error.ResourceNotFoundException;
import com.luckin.javademo.persistence.EchoMessageEntity;
import com.luckin.javademo.persistence.EchoMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
}


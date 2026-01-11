package com.luckin.javademo.service;

import com.luckin.javademo.persistence.EchoMessageEntity;
import com.luckin.javademo.persistence.EchoMessageRepository;
import org.springframework.stereotype.Service;

/**
 * Echo 业务服务：
 * - 承载与协议无关的业务处理（示例中：计算长度、组织回显结果）
 * - 不关心 HTTP/状态码/请求体解析；这些属于 Controller 的协议层职责
 */
@Service
public class EchoService {
    private final EchoMessageRepository echoMessageRepository;

    public EchoService(EchoMessageRepository echoMessageRepository) {
        this.echoMessageRepository = echoMessageRepository;
    }

    /**
     * 业务含义：对输入消息进行回显，并给出长度。
     *
     * 边界说明：
     * - 参数合法性（例如空字符串、全空白、缺失）应在 Controller 层完成校验并返回 400；
     * - 这里默认调用方已完成校验，仅做纯业务计算，避免把协议规则散落在多层。
     */
    public EchoResult echo(String message) {
        EchoResult result = new EchoResult(message, message.length());

        // 最小持久化闭环：将一次 echo 请求落库到 echo_message 表
        // - 不改变现有接口响应，只为 Day7/Day8 演示连库与实体映射
        echoMessageRepository.save(new EchoMessageEntity(result.message(), result.length()));

        return result;
    }

    /**
     * Service 层返回的业务结果对象：
     * - 不直接复用 Controller DTO，避免协议层模型“渗透”到业务层
     */
    public record EchoResult(String message, int length) {}
}


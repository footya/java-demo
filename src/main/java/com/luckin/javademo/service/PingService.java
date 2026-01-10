package com.luckin.javademo.service;

import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Ping 业务服务：
 * - 示例服务，用于体现“Controller 只做协议层”的分层约束
 * - 当前业务极简，但依然用 Service 承载返回值，便于后续扩展（例如健康检查维度、依赖探测等）
 */
@Service
public class PingService {
    public String ping() {
        return "pong";
    }

    public Map<String, Object> pingJson() {
        return Map.of("message", "pong");
    }
}


package com.luckin.javademo.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * echo_message 表的实体映射：
 * - 该实体只负责“表字段 <-> Java 字段”的映射，不承载 HTTP 协议含义
 * - createdAt 由数据库默认值写入（CURRENT_TIMESTAMP），业务侧通常不手动赋值
 */
@Entity
@Table(name = "echo_message")
public class EchoMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String message;

    @Column(nullable = false)
    private Integer length;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected EchoMessageEntity() {}

    public EchoMessageEntity(String message, Integer length) {
        this.message = message;
        this.length = length;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Integer getLength() {
        return length;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新业务字段：
     * - createdAt 由数据库维护，不允许业务侧修改
     * - 仅用于 CRUD 的“更新”场景，避免对外暴露通用 setter 导致误用
     */
    public void updateMessageAndLength(String message, Integer length) {
        this.message = message;
        this.length = length;
    }
}


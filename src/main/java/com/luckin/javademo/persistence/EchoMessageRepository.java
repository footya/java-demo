package com.luckin.javademo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * echo_message 的数据访问层（Repository）：
 * - 通过 Spring Data JPA 生成最小 CRUD
 * - Service 通过该接口落库/查询，避免把持久化细节散落到 Controller
 */
@Repository
public interface EchoMessageRepository extends JpaRepository<EchoMessageEntity, Long> {
}


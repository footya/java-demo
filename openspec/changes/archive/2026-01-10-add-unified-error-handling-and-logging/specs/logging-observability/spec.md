## ADDED Requirements

### Requirement: 请求链路标识（traceId）
系统 MUST 为每个请求建立链路标识（traceId），用于关联日志与错误响应，并支持调用方通过请求头传入。

#### Scenario: 调用方传入 request id
- **GIVEN** 客户端请求包含请求头 `X-Request-Id`
- **WHEN** 服务端处理该请求
- **THEN** 系统 MUST 使用该值作为 `traceId`
- **AND** 响应头 MUST 回传同一个 `X-Request-Id`
- **AND** 错误响应体（若发生错误）字段 `traceId` MUST 等于该值

#### Scenario: 调用方未传入 request id
- **GIVEN** 客户端请求不包含请求头 `X-Request-Id`
- **WHEN** 服务端处理该请求
- **THEN** 系统 MUST 生成新的 `traceId`
- **AND** 响应头 MUST 回传 `X-Request-Id`
- **AND** 错误响应体（若发生错误）字段 `traceId` MUST 等于该值

### Requirement: 关键链路日志（入参摘要/耗时/错误）
系统 MUST 记录关键链路日志，以便在不泄漏敏感信息的前提下定位问题与评估耗时。

#### Scenario: 成功请求日志
- **GIVEN** 系统处理一个成功请求并返回 2xx
- **WHEN** 请求完成
- **THEN** 系统 MUST 记录至少一条包含 `traceId`、`method`、`path`、`status`、`durationMs` 的日志
- **AND** 日志 MUST NOT 记录敏感信息（如 token、key、密码等）

#### Scenario: 错误请求日志
- **GIVEN** 系统处理一个请求并返回 4xx/5xx
- **WHEN** 请求完成
- **THEN** 系统 MUST 记录错误日志且包含 `traceId` 与异常信息
- **AND** 对于 5xx，系统 MUST 记录异常栈以便排障
- **AND** 日志 MUST NOT 记录敏感信息（如 token、key、密码等）


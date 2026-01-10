## ADDED Requirements

### Requirement: 标准化错误响应与错误码
系统 MUST 在发生错误时返回标准化 JSON 响应，并提供稳定的错误码（`code`）以便调用方可靠识别错误类型。

#### Scenario: 参数缺失或不合法（通用 400）
- **GIVEN** 系统提供任意 HTTP 接口
- **WHEN** 客户端请求参数缺失/为空白/不满足接口约束，导致服务端判定为 `400`
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体 MUST 包含字段 `code` 且值为 `INVALID_REQUEST`
- **AND** 响应体 MUST 包含字段 `message`（不包含敏感信息/内部细节）
- **AND** 响应体 MUST 包含字段 `traceId`
- **AND** 响应体 MUST 包含字段 `path`
- **AND** 响应体 MUST 包含字段 `timestamp`

#### Scenario: 字段校验失败（校验错误数组）
- **GIVEN** 系统对请求入参启用校验（如 Bean Validation 或绑定校验）
- **WHEN** 客户端请求触发校验失败，导致服务端判定为 `400`
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体字段 `code` MUST 等于 `VALIDATION_FAILED`
- **AND** 响应体 MAY 包含字段 `errors`
- **AND** 若返回 `errors`，其 MUST 为数组且每个元素 MUST 包含 `field` 与 `message`

#### Scenario: 媒体类型不支持（415）
- **GIVEN** 系统存在仅接受 `application/json` 的接口
- **WHEN** 客户端以非 JSON 的 `Content-Type` 请求该接口导致 `415`
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体字段 `code` MUST 等于 `UNSUPPORTED_MEDIA_TYPE`

#### Scenario: 未捕获异常（500）
- **GIVEN** 系统运行中
- **WHEN** 服务端发生未捕获异常导致 `500`
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体字段 `code` MUST 等于 `INTERNAL_ERROR`
- **AND** 响应体字段 `message` MUST 为通用提示且 MUST NOT 包含异常堆栈/内部实现细节
- **AND** 响应体 MUST 包含字段 `traceId` 以便定位日志


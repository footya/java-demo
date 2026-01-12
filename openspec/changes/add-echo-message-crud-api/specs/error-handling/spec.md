## ADDED Requirements

### Requirement: 资源不存在（404）
系统 MUST 在资源不存在时返回标准化 JSON 错误响应，并提供稳定的错误码以便调用方可靠识别该类错误。

#### Scenario: 按 id 查询资源不存在
- **GIVEN** 系统提供任意按 id 查询的 HTTP 接口
- **WHEN** 客户端请求的资源不存在导致服务端判定为 `404`
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体字段 `code` MUST 等于 `NOT_FOUND`
- **AND** 响应体 MUST 包含字段 `message`（不包含敏感信息/内部细节）
- **AND** 响应体 MUST 包含字段 `traceId`
- **AND** 响应体 MUST 包含字段 `path`
- **AND** 响应体 MUST 包含字段 `timestamp`


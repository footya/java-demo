# api-response Specification

## Purpose
TBD - created by archiving change add-echo-message-crud-api. Update Purpose after archive.
## Requirements
### Requirement: 统一成功响应结构（JSON 外层封装）
系统 MUST 为成功响应提供统一的 JSON 外层结构，以便调用方用同一套逻辑处理不同接口的成功返回。

#### Scenario: 任意成功响应返回统一外层结构
- **GIVEN** 系统提供任意 HTTP 接口
- **WHEN** 客户端请求被服务端判定为成功（2xx）
- **THEN** 响应体 MUST 为 JSON
- **AND** 响应体 MUST 包含字段 `code`
- **AND** 响应体 MUST 包含字段 `message`
- **AND** 响应体 MUST 包含字段 `traceId`
- **AND** 响应体 MAY 包含字段 `data`

#### Scenario: 统一成功码
- **GIVEN** 系统提供任意 HTTP 接口
- **WHEN** 客户端请求被服务端判定为成功（2xx）
- **THEN** 响应体字段 `code` MUST 等于 `OK`


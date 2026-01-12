# echo-api Specification

## Purpose
TBD - created by archiving change add-echo-endpoint. Update Purpose after archive.
## Requirements
### Requirement: Echo 接口
系统 MUST 提供一个示例接口用于展示 Controller、路由、请求体/响应体与状态码处理。

#### Scenario: 正常回显
- **GIVEN** 服务运行中
- **WHEN** 客户端以 `Content-Type: application/json` 调用 `POST /echo` 且请求体为 `{"message":"hi"}`
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON
- **AND** 响应体包含字段 `message` 且等于 `"hi"`
- **AND** 响应体包含字段 `length` 且等于 `2`

#### Scenario: 请求体缺失或 message 为空
- **GIVEN** 服务运行中
- **WHEN** 客户端调用 `POST /echo` 且 `message` 缺失/为 `null`/为空字符串/仅空白
- **THEN** 响应状态码为 `400`

#### Scenario: Content-Type 非 JSON
- **GIVEN** 服务运行中
- **WHEN** 客户端调用 `POST /echo` 且 `Content-Type` 非 `application/json`
- **THEN** 响应状态码为 `415`

### Requirement: EchoMessage 的 CRUD 接口
系统 MUST 提供面向资源 `echo_message` 的最小 CRUD 接口集合（新增/查询/更新/删除各 1 个），用于覆盖 Day9「CRUD 接口」训练目标；接口成功返回 MUST 遵循统一成功响应结构（见 `api-response`）。

#### Scenario: 新增（Create）
- **GIVEN** 服务运行中
- **WHEN** 客户端以 `Content-Type: application/json` 调用 `POST /echo-messages` 且请求体为 `{"message":"hi"}`
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON 且符合统一成功响应结构
- **AND** 响应体字段 `data` MUST 包含 `id`
- **AND** 响应体字段 `data.message` MUST 等于 `"hi"`
- **AND** 响应体字段 `data.length` MUST 等于 `2`
- **AND** 响应体字段 `data.createdAt` MUST 存在

#### Scenario: 查询（Read）
- **GIVEN** 服务运行中且已存在一条 `echo_message` 记录（id 为 1）
- **WHEN** 客户端调用 `GET /echo-messages/1`
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON 且符合统一成功响应结构
- **AND** 响应体字段 `data.id` MUST 等于 `1`

#### Scenario: 更新（Update）
- **GIVEN** 服务运行中且已存在一条 `echo_message` 记录（id 为 1）
- **WHEN** 客户端以 `Content-Type: application/json` 调用 `PUT /echo-messages/1` 且请求体为 `{"message":"hello"}`
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON 且符合统一成功响应结构
- **AND** 响应体字段 `data.id` MUST 等于 `1`
- **AND** 响应体字段 `data.message` MUST 等于 `"hello"`
- **AND** 响应体字段 `data.length` MUST 等于 `5`

#### Scenario: 删除（Delete）
- **GIVEN** 服务运行中且已存在一条 `echo_message` 记录（id 为 1）
- **WHEN** 客户端调用 `DELETE /echo-messages/1`
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON 且符合统一成功响应结构

#### Scenario: 查询不存在的资源返回 404
- **GIVEN** 服务运行中且不存在 `echo_message` 记录（id 为 999999）
- **WHEN** 客户端调用 `GET /echo-messages/999999`
- **THEN** 响应状态码为 `404`
- **AND** 响应体 MUST 符合标准化错误响应（见 `error-handling`）
- **AND** 响应体字段 `code` MUST 等于 `NOT_FOUND`


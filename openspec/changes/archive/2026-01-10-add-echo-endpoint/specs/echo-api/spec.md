## ADDED Requirements

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

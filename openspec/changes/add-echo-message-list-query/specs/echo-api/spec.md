## ADDED Requirements
### Requirement: EchoMessage 列表查询接口（分页/排序/条件过滤）
系统 MUST 提供 `echo_message` 资源的列表查询接口 `GET /echo-messages`，用于支持分页参数与基础条件过滤；接口成功响应 MUST 遵循统一成功响应结构（见 `api-response`），并在 `data` 中返回列表数据与分页信息。

#### Scenario: 默认分页查询成功
- **GIVEN** 服务运行中且已存在多条 `echo_message` 记录
- **WHEN** 客户端调用 `GET /echo-messages`（不传任何查询参数）
- **THEN** 响应状态码为 `200`
- **AND** 响应体为 JSON 且符合统一成功响应结构
- **AND** 响应体字段 `data.items` MUST 为数组
- **AND** `data.items` 的数组元素 MUST 包含字段 `id`、`message`、`length`、`createdAt`
- **AND** 响应体字段 `data.page.page` MUST 等于 `1`
- **AND** 响应体字段 `data.page.size` MUST 存在
- **AND** 响应体字段 `data.page.totalElements` MUST 存在
- **AND** 响应体字段 `data.page.totalPages` MUST 存在

#### Scenario: 按 message 关键字过滤
- **GIVEN** 服务运行中且存在多条 `echo_message` 记录，其中部分记录的 `message` 包含子串 `hi`
- **WHEN** 客户端调用 `GET /echo-messages?message=hi`
- **THEN** 响应状态码为 `200`
- **AND** 响应体字段 `data.items` 中每条记录的 `message` MUST 包含子串 `hi`

#### Scenario: 按创建时间范围过滤
- **GIVEN** 服务运行中且已存在多条 `echo_message` 记录
- **WHEN** 客户端调用 `GET /echo-messages?createdAtFrom=2026-01-01T00:00:00Z&createdAtTo=2026-02-01T00:00:00Z`
- **THEN** 响应状态码为 `200`
- **AND** 响应体字段 `data.items` 中每条记录的 `createdAt` MUST 落在该时间范围内（含边界）

#### Scenario: 分页参数与排序参数生效
- **GIVEN** 服务运行中且已存在多条 `echo_message` 记录
- **WHEN** 客户端调用 `GET /echo-messages?page=2&size=10&sort=createdAt&order=desc`
- **THEN** 响应状态码为 `200`
- **AND** 响应体字段 `data.page.page` MUST 等于 `2`
- **AND** 响应体字段 `data.page.size` MUST 等于 `10`
- **AND** `data.items` 的长度 MUST 小于等于 `10`
- **AND** `data.items` MUST 按 `createdAt` 倒序排列

#### Scenario: 非法查询参数返回 400
- **GIVEN** 服务运行中
- **WHEN** 客户端调用 `GET /echo-messages?page=0` 或 `GET /echo-messages?size=0`
- **THEN** 响应状态码为 `400`
- **AND** 响应体 MUST 符合标准化错误响应（见 `error-handling`）


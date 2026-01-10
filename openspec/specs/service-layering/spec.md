# service-layering Specification

## Purpose
TBD - created by archiving change refactor-controller-service-layering. Update Purpose after archive.
## Requirements
### Requirement: Controller 与 Service 分层
系统 MUST 遵循分层约束：Controller 只负责协议层，业务规则与业务编排 MUST 下沉到 Service，并通过依赖注入连接各层。

#### Scenario: Echo 接口采用薄 Controller
- **GIVEN** 系统提供 `POST /echo`
- **WHEN** 代码按分层约束实现
- **THEN** `EchoController` 只做入参解析/校验与响应封装，并把回显与长度计算交由 `EchoService` 完成

#### Scenario: Weather 接口采用薄 Controller
- **GIVEN** 系统提供 `GET /weather`
- **WHEN** 代码按分层约束实现
- **THEN** `WeatherController` 只做入参解析/校验与响应封装，并把城市到 adcode、天气查询、穿衣建议等编排交由 `WeatherService` 完成

#### Scenario: 依赖注入使用构造器注入
- **GIVEN** Controller/Service 依赖外部组件（如 client、advisor）
- **WHEN** 代码按分层约束实现
- **THEN** 依赖 MUST 通过构造器注入显式声明，避免字段注入与静态访问


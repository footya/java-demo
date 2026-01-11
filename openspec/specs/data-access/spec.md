# data-access Specification

## Purpose
TBD - created by archiving change add-jpa-h2-echo-persistence. Update Purpose after archive.
## Requirements
### Requirement: 本地数据库接入（默认 H2）
系统 MUST 提供本地可用的数据库接入能力：在默认配置下（无需额外安装数据库）即可启动并连接到 H2 数据库；并允许通过配置覆盖切换到本地 MySQL 等外部数据库。

#### Scenario: 默认配置下应用可启动并连通数据库
- **GIVEN** 开发者在本地启动应用且未提供额外的数据库连接配置
- **WHEN** 应用完成启动流程
- **THEN** 系统 MUST 成功连接到默认 H2 数据库并完成初始化

#### Scenario: 通过配置覆盖切换到外部数据库
- **GIVEN** 开发者提供 MySQL 的连接配置（URL/用户名/密码等）
- **WHEN** 应用完成启动流程
- **THEN** 系统 MUST 使用该配置连接到外部数据库

### Requirement: 核心表 `echo_message`（建表）
系统 MUST 定义 1 张核心表 `echo_message`，用于存储一次回显请求的最小持久化信息，并包含必要的主键与时间字段。

#### Scenario: 初始化时创建 `echo_message` 表
- **GIVEN** 系统在启动时执行数据库初始化
- **WHEN** 初始化完成
- **THEN** 数据库 MUST 存在表 `echo_message`
- **AND** 表 MUST 至少包含字段 `id`、`message`、`length`、`created_at`

### Requirement: `echo_message` 的实体映射与 Repository
系统 MUST 提供 `echo_message` 的实体映射（Entity）与数据访问接口（Repository），以便对核心表进行最小 CRUD 验证。

#### Scenario: 保存并读取一条 `echo_message` 记录
- **GIVEN** 系统已连接到数据库且已创建 `echo_message` 表
- **WHEN** 通过 Repository 保存一条包含 `message` 与 `length` 的记录
- **THEN** 系统 MUST 能通过主键读取到该记录
- **AND** 读取结果字段 `message` 与 `length` MUST 与保存时一致


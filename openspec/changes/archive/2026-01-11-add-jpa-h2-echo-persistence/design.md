## Context
工程当前仅包含 Web/Validation 能力与若干示例接口，尚未引入数据库依赖、数据源配置、建表与实体映射能力。学习目标要求在不引入过多复杂度的前提下完成「选型与连库」以及「建表与实体映射」的最小可运行闭环。

## Goals / Non-Goals
- Goals:
  - 明确 ORM 选型，并完成最小可用落地（依赖 + 配置 + 启动可连通）
  - 默认使用 H2 覆盖本地学习路径，同时保留切换到本地 MySQL 的配置扩展点
  - 至少 1 张核心表 + Entity + Repository，能够完成最小 CRUD 验证映射可用
- Non-Goals:
  - 不引入复杂的迁移框架与多环境部署策略（如 Flyway/Liquibase、生产级迁移流程）
  - 不强制改造现有 API 行为（本提案聚焦数据层接入与映射，API 是否落库由后续任务决定）

## Decisions
- ORM 选型：Spring Data JPA
  - 理由：与 Spring Boot 生态契合、学习资料丰富、样板代码少，适合 Day7/Day8 的最小闭环
  - 备选：MyBatis
    - 取舍：更贴近 SQL 与可控性，但需要 Mapper/XML 或注解、配置项更多，不利于当前阶段快速闭环
- 本地数据库：H2（默认）
  - 理由：零安装、启动快，适合本地学习与 CI 编译验证
  - 扩展点：通过 profile/配置覆盖支持切换到本地 MySQL（URL/用户名/密码/方言等）
- 核心表选择：`echo_message`
  - 理由：与现有 `POST /echo` 示例接口天然关联，字段简单（message/length/时间），适合讲清楚建表与实体映射

## Risks / Trade-offs
- H2 与 MySQL 的 SQL/方言差异：建表脚本与字段类型需避免强依赖某一种实现（例如时间类型、关键字）
- JPA 自动建表策略的误用：学习阶段可用于快速验证，但需要在任务中约束 `ddl-auto` 的使用边界，避免不可控的 schema 变更

## Migration Plan
- 第一步引入 H2 本地默认配置，确保应用可启动并初始化 schema
- 第二步补齐 `echo_message` 表与 JPA 映射
- 可选：增加 MySQL 本地 profile（仅配置层面），用于后续切换验证

## Open Questions
- 是否希望 Day7/Day8 的实现最终落在 **JPA**（默认）还是改为 **MyBatis**？
- 本地数据库偏好：**H2**（默认）还是直接连接 **本地 MySQL**？


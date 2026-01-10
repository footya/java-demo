# Change: Day4 - Service 分层与依赖注入

## Why
当前部分接口将业务规则与编排逻辑直接写在 Controller 中，难以复用、难以单测、也不利于后续引入更复杂的业务与错误处理规范。

## What Changes
- 将业务规则与编排逻辑下沉到 Service；Controller 仅负责协议层（路由、参数解析与校验、DTO 转换、返回值封装）。
- 使用依赖注入（构造器注入）让 Controller 依赖 Service；Service 依赖外部 client/adapter。
- 保持现有接口对外行为不变（路径、方法、成功/失败状态码与响应结构）。

## Impact
- Affected specs: 新增 capability `service-layering`（代码结构/分层约束）；现有对外接口 spec 不做行为变更。
- Affected code: `src/main/java/com/luckin/javademo/*Controller.java` 及新增对应 `*Service`。


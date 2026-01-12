# Change: Day9 - EchoMessage 的 CRUD 接口（新增/查询/更新/删除）与统一成功返回结构

## Why
当前工程仅有示例类接口（如 `POST /echo`），缺少完整的 CRUD 练习面向资源的接口集合，无法覆盖 Day9「CRUD 接口」的训练目标；同时成功返回结构未统一，调用方在接入多个接口时需要做分支兼容。

## What Changes
- 新增 4 个 CRUD 接口（各 1 个）：对资源 `echo_message` 提供新增/查询/更新/删除能力。
- 新增“统一成功返回结构”规范：成功响应统一采用同一 JSON 外层结构（包含 `code`、`message`、`traceId`、`data`）。
- 补充 404（资源不存在）的标准化错误码要求，避免 CRUD 场景下错误返回不一致。

## Impact
- Affected specs: `echo-api`, `api-response`, `error-handling`
- Affected code: `src/main/java/com/luckin/javademo/**`（预计新增/调整 controller/service/dto 与错误码映射），`src/main/resources/**`（如需要配置调整）


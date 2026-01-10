# Change: 增加一个最小示例接口（涵盖 Controller/路由/请求体/响应体/状态码）

## Why
当前项目已有 GET 示例与 query 参数校验，但缺少 POST + JSON 请求体 + 明确响应体 + 常见状态码的最小可参考实现。

## What Changes
- 增加一个 `POST /echo` 接口规范：请求体、响应体、校验与状态码行为。

## Impact
- Affected specs: `echo-api`
- Affected code: `src/main/java/com/luckin/javademo/`（新增一个 Controller 或在现有 Controller 中扩展）

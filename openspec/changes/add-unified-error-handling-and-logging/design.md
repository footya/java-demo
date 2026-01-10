## Context
本变更将“异常处理/错误响应/错误码/日志链路”从零补齐，形成一个可复用的最小工程化基座，避免每个 Controller 自行抛错与返回，且保证排障时能够用同一个 traceId 串起“请求—处理—错误”。

## Goals / Non-Goals
- Goals:
  - 统一错误响应结构（JSON）与错误码体系，覆盖常见 4xx/5xx。
  - 全局异常处理集中收敛：参数校验、协议解析、业务异常（含显式状态码）与兜底异常。
  - 关键链路日志：入参摘要、耗时、状态码、错误栈；支持 traceId 贯穿。
  - 安全：敏感信息不入日志；错误响应不泄漏内部堆栈/实现细节。
- Non-Goals:
  - 不引入完整链路追踪/指标/分布式追踪系统（仅做最小可观测）。
  - 不强制所有接口立即迁移到 Bean Validation（可渐进）。

## Decisions
- 错误码（`code`）采用稳定字符串枚举，独立于 HTTP status：
  - `INVALID_REQUEST`：通用 400（入参缺失/格式不对/业务拒绝等）。
  - `VALIDATION_FAILED`：Bean Validation 或绑定校验失败（400），可返回字段级错误数组。
  - `UNSUPPORTED_MEDIA_TYPE`：415。
  - `INTERNAL_ERROR`：500，消息固定为通用提示，不回传异常堆栈。
- 标准错误响应结构（JSON）定义为：
  - `code`：错误码（字符串）
  - `message`：面向调用方的可读提示（不包含敏感信息/内部细节）
  - `traceId`：请求链路标识（与日志一致），便于定位
  - `path`：请求路径
  - `timestamp`：服务端时间戳（ISO-8601）
  - `errors`（可选）：字段级错误数组，元素包含 `field` 与 `message`
- traceId 采用请求头 `X-Request-Id`：
  - 若客户端传入则沿用；否则服务端生成。
  - 需要写入 MDC，日志格式中输出，并在错误响应体与响应头回传。
- 关键链路日志最小字段集合：
  - `traceId`、`method`、`path`、`status`、`durationMs`
  - 入参仅记录“摘要”，避免记录请求体原文；必要时对值脱敏或只记录长度/字段名。

## Risks / Trade-offs
- 统一错误响应可能改变 Spring 默认错误格式，对已有调用方属于兼容性风险；但本工程以示例为主，收益更大。
- 入参日志“摘要化”降低了信息量，但可以显著降低敏感信息泄漏风险；需要时再做白名单增强。

## Migration Plan
- 先落地全局异常处理与统一错误响应，保持现有 Controller 抛错方式不变（如 `ResponseStatusException`）。
- 再补齐 traceId 与关键链路日志，逐步将校验迁移到 Bean Validation（可选）。

## Open Questions
- `X-Request-Id` 的命名是否需要改为 `X-Trace-Id` 以减少歧义？
- `errors` 字段是否需要在所有 400 场景都返回，还是仅校验失败场景返回？


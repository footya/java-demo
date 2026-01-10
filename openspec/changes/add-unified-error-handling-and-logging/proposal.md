# Change: 统一异常与错误码 + 日志与可观测最小集

## Why
当前工程缺少全局异常处理与统一错误响应结构，导致 4xx/5xx 返回不一致、错误码缺失，客户端与排障都难以稳定对齐。同时缺少关键链路日志（入参摘要/耗时/错误/traceId），线上问题难以定位与关联。

## What Changes
- 增加全局异常处理（`@ControllerAdvice`），标准化错误响应结构并引入错误码体系。
- 增加最小可观测能力：请求链路标识（traceId）与关键链路日志（入参摘要/耗时/状态码/错误）。

## Impact
- Affected specs: `error-handling`, `logging-observability`
- Affected code: `src/main/java/com/luckin/javademo/`（新增全局异常处理、错误码与日志链路基础设施），`src/main/resources/application.properties`（日志格式/traceId 展示）


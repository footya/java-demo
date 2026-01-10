## Context
本项目采用 Spring Boot Web MVC，当前已有 `EchoController`、`WeatherController`、`PingController`。其中 `EchoController` 与 `WeatherController` 中存在可识别的业务规则/编排逻辑，适合下沉到 Service 以提升可维护性与职责清晰度。

## Goals / Non-Goals
- Goals:
  - Controller 只做协议层；业务规则/编排逻辑集中在 Service。
  - 统一使用构造器注入，便于测试与依赖显式化。
  - 对外 API 行为保持不变。
- Non-Goals:
  - 不引入新的业务功能、不新增接口。
  - 不做大规模包结构重构（只改为达成分层所需的最小范围）。

## Decisions
- Decision: 采用“Controller → Service → client/adapter”的调用链。
  - Controller 仅负责：路由、参数解析与校验、DTO 转换、HTTP 响应结构封装。
  - Service 负责：业务规则、编排、容错边界（必要时对外部 client 的异常进行语义化转换）。
- Decision: 依赖注入采用构造器注入（Spring 默认支持），避免字段注入。

## Risks / Trade-offs
- 风险：将逻辑下沉后，若 Controller/Service 边界不清可能造成 DTO 与领域对象混用。
  - 缓解：明确 DTO 仅在 Controller 层使用；Service 方法参数/返回优先使用领域语义或内部 POJO（如确需复用 DTO，需评估稳定性与暴露风险）。

## Migration Plan
先新增 Service 并在不改行为前提下迁移逻辑，再逐个 Controller 改造成薄 Controller，最后做一次编译验证。

## Open Questions
- 是否需要引入统一异常处理（`@RestControllerAdvice`）来替代 Controller 内的 `ResponseStatusException`？本次变更以“最小必要范围”为主，默认不强制引入，除非为了保持行为一致或减少重复校验代码。


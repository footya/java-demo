## Java/Spring Boot 代码规范（本项目）

## 分层与职责

- Controller：只做协议层（路由、参数解析/校验、DTO 转换、返回值封装），不写业务规则。
- Service：承载业务规则与编排；保持方法短小、意图清晰。
- 外部调用（如 HTTP API）：封装为 client 类；显式处理超时、失败、降级与重试策略（如需要）。

## 命名与结构

- 包与类命名：语义明确；避免缩写堆叠。
- DTO：用于接口入参/出参；字段命名与 JSON 映射一致；为字段补充中文注释说明含义/单位/取值范围。
- 领域对象：不要直接暴露为接口返回（除非明确无风险且稳定）。

## 参数校验与错误处理

- 入参校验：优先使用 Jakarta Bean Validation（`@Valid` + 约束注解），在 Controller 层触发。
- 统一异常处理：使用 `@RestControllerAdvice`；错误响应结构稳定、字段含义清晰；不要把堆栈直接返回给前端。
- 对外错误：避免泄露内部实现细节（类名、SQL、堆栈、网关地址等）。

## 日志

- 使用 SLF4J 参数化日志：`log.info("xxx={}, yyy={}", x, y)`。
- 日志应可检索：关键信息写字段，不要拼接长句；异常日志要包含上下文。
- 敏感信息脱敏：token/key/手机号等不得原样入日志。

## 时间与并发

- 时间使用 `java.time`（`Instant/OffsetDateTime/LocalDateTime` 等），避免 `Date`/`Calendar`。
- 并发与共享状态：避免在 Controller/Service 使用可变全局变量；如必须缓存，明确线程安全策略。

## 依赖与配置

- 配置集中在 `application.properties`/`application.yml` 与环境变量；不要硬编码密钥。
- 新增依赖需有明确必要性；优先使用 Spring Boot 官方推荐方式。

## 可执行性约束

- 每次改动后，应保证能通过 `mvn -q -DskipTests package` 编译。

## 参考

- Google Java Style Guide：`https://google.github.io/styleguide/javaguide.html`
- 阿里巴巴《Java开发手册》：`https://github.com/alibaba/p3c`
- Spring Boot Reference：`https://docs.spring.io/spring-boot/reference/`
- Spring Framework Web MVC：`https://docs.spring.io/spring-framework/reference/web/webmvc.html`
- Jakarta Bean Validation：`https://jakarta.ee/specifications/bean-validation/`
- SLF4J Manual：`https://www.slf4j.org/manual.html`
- OWASP Logging Cheat Sheet：`https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html`

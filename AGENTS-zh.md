<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

## 构建与测试命令

### 核心命令
- **构建打包**: `mvn -q -DskipTests package`
- **运行开发服务器**: `mvn -q spring-boot:run` 或 `./dev.sh <AMAP_KEY> [port]`
- **运行 JAR**: `java -jar target/java-demo-0.0.1-SNAPSHOT.jar`
- **快速开发**: `./start.sh <AMAP_KEY> [port]`

### 测试
- 本项目当前没有测试目录（`src/test/` 不存在）
- 添加测试时使用 JUnit 5（Spring Boot 3.2.1 的标准）
- 单个测试示例（未来）：`mvn test -Dtest=ClassName#methodName`

## 代码风格指南

### 分层架构
- **Controller**: 仅协议层 - 路由、参数解析/校验、DTO 转换、响应封装。不包含业务逻辑。
- **Service**: 业务逻辑和编排。保持方法简短、意图清晰。使用领域对象，而非 DTO。
- **Client 类**: 外部 HTTP/API 调用封装在专门的客户端类中。显式处理超时、失败、降级、重试。
- **DTO**: 用于接口输入/输出。字段命名与 JSON 映射一致。领域对象不作为 API 返回值。

### 命名规范
- 包和类：语义明确，避免缩写堆叠
- DTO：用途清晰，与 JSON 字段名保持一致
- 领域对象：内部业务模型，与协议 DTO 分离

### 导入组织
- 分组导入：标准库、第三方库，然后是内部项目
- 谨慎使用 `import com.luckin.javademo.*;` - 优先使用显式导入
- 避免第三方包的通配符导入

### 类型与语言特性
- 需要 Java 17
- 使用 `java.time` 类型（Instant、OffsetDateTime、LocalDateTime）- 避免使用 Date/Calendar
- 使用 `record` 作为不可变数据载体（DTO、响应、内部结果）
- 避免在 controller/service 中使用可变的静态/全局状态
- 使用 `@Value` 进行配置注入，不在字段上使用 `@Autowired`

### 参数校验
- 在 Controller 层使用 Jakarta Bean Validation（`@Valid` + 约束注解如 `@NotBlank`、`@NotNull`）
- 在 Controller 中校验请求参数，无效输入返回 400
- Service 方法假定输入有效 - 仅关注业务逻辑

### 错误处理
- 通过 `@RestControllerAdvice` 集中处理（参见 `GlobalExceptionHandler`）
- 返回结构化错误响应，包含 `code/message/traceId/path/timestamp`
- 绝不暴露内部细节：不在客户端响应中包含堆栈跟踪、类名、SQL、URL 或令牌
- 使用标准 Spring 异常：`ResponseStatusException`、`MethodArgumentNotValidException`
- 对于 4xx：返回通用客户端友好的消息
- 对于 5xx：返回"系统繁忙，请稍后再试"，记录完整堆栈跟踪和 traceId

### 日志 (SLF4J)
- 使用参数化日志：`log.info("city={}, temp={}", city, temp)` - 不要使用字符串拼接
- 记录关键字段以便检索，不要记录长句
- 在错误日志中包含上下文：traceId、method、path
- **禁止使用 System.out.println** - 使用 logger
- **敏感数据**：绝不记录令牌、密钥、电话号码、ID - 掩码或省略

### 安全
- 不硬编码机密信息：使用 `application.properties`/`application.yml` 或环境变量
- 外部 API 密钥通过 `@Value("${amap.key:}")` 模式注入
- 日志脱敏：用星号替换敏感部分

### 注释风格（中文）
- 为以下内容添加充分的中文注释：类/方法职责、关键业务分支、协议字段含义、异常原因和处理策略
- 避免低价值注释（只是重复代码在做什么）
- 注释边界条件和边缘情况
- 解释为什么，而不仅仅是做什么

### 验证
- 每次更改必须通过：`mvn -q -DskipTests package`
- 做最小化更改以满足需求 - 除非要求，否则不要重构/格式化整个项目
- 避免不必要的框架或模式 - 优先选择简单、可读、可测试的代码

### 参考文献
- Google Java 风格指南: https://google.github.io/styleguide/javaguide.html
- 阿里巴巴 Java 手册: https://github.com/alibaba/p3c
- Spring Boot 参考文档: https://docs.spring.io/spring-boot/reference/
- Jakarta Bean Validation: https://jakarta.ee/specifications/bean-validation/

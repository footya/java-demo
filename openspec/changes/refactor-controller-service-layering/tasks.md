## 1. Implementation
- [x] 1.1 梳理现有 Controller 的“业务逻辑”边界：`EchoController`/`WeatherController`/`PingController` 各自应下沉到 Service 的职责清单
- [x] 1.2 新增 Service：`EchoService`、`WeatherService`、（如需统一）`PingService`；采用构造器注入
- [x] 1.3 Controller 改造：仅保留路由、入参解析/校验、DTO 转换、调用 Service、组装返回
- [x] 1.4 保持对外行为不变：对照现有接口返回值/状态码，确认无破坏性改动
- [x] 1.5 `mvn -q -DskipTests package` 编译通过


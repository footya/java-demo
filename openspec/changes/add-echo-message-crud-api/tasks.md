## 1. Implementation
- [x] 1.1 定义统一成功返回结构 DTO（含 `code/message/traceId/data`）并在 CRUD 接口中应用
- [x] 1.2 新增 EchoMessage CRUD 的 Controller（协议层）与路由（POST/GET/PUT/DELETE 各 1 个）
- [x] 1.3 新增/调整 Service（业务层）：创建、按 id 查询、按 id 更新、按 id 删除
- [x] 1.4 对 CRUD 入参添加 Bean Validation（message 不能为空白、id 合法等）并复用全局错误处理
- [x] 1.5 补充 404 场景：资源不存在时返回标准错误码与标准错误响应
- [x] 1.6 本地编译通过：`mvn -q -DskipTests package`


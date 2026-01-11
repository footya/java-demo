## 1. Implementation
- [ ] 1.1 依赖选型落地：引入 Spring Data JPA 与 H2（本地默认）；保留未来切换本地 MySQL 的扩展点（profile/配置覆盖）
- [ ] 1.2 配置连通：补齐 datasource/JPA 的本地配置，确保应用启动时可以连接并初始化数据库
- [ ] 1.3 建表与映射：新增核心表 `echo_message` 的建表脚本与 Entity 映射（字段、主键、时间字段）
- [ ] 1.4 Repository：新增 `echo_message` 的 Repository（最小 CRUD），用于验证实体映射可用
- [ ] 1.5 校验：`mvn -q -DskipTests package` 编译通过


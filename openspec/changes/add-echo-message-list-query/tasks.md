## 1. Implementation
- [x] 1.1 为 `GET /echo-messages` 定义请求参数解析与 Bean Validation（分页/排序/过滤）
- [x] 1.2 在 Service 层实现列表查询：分页（PageRequest）、排序（Sort）、条件过滤（message contains、createdAt 区间）
- [x] 1.3 在 Controller 层新增列表路由，只做协议层职责，返回 `data.items` 与 `data.page`
- [x] 1.4 运行 `mvn -q -DskipTests package` 确认可编译


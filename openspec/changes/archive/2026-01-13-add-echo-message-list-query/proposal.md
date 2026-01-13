# Change: Day10 EchoMessage 列表接口支持分页、排序、条件查询

## Why
当前 `echo_message` 资源只有单条 CRUD（按 id 查询/更新/删除），缺少列表接口，无法覆盖分页、排序与基础条件过滤的训练目标。

## What Changes
- 新增 `GET /echo-messages` 列表接口，支持分页参数（`page`/`size`）
- 新增基础排序参数（`sort`/`order`）
- 新增基础条件过滤参数（按 `message` 关键字、按创建时间范围）
- 列表接口成功响应继续遵循统一成功响应结构（见 `api-response`），分页信息放入 `data`

## Impact
- Affected specs: `echo-api`
- Affected code (implement stage):
  - `src/main/java/com/luckin/javademo/EchoMessageController.java`
  - `src/main/java/com/luckin/javademo/service/EchoMessageCrudService.java`（可能新增列表查询能力或拆分新 Service）
  - `src/main/java/com/luckin/javademo/persistence/EchoMessageRepository.java`（可能扩展查询能力）


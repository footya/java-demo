# Change: 增加本地数据库接入（默认 H2）与核心表实体映射（Spring Data JPA）

## Why
当前工程未接入任何数据库，无法覆盖 Day7「选型与连库」和 Day8「建表与实体映射」的学习目标。

## What Changes
- 选型：默认采用 Spring Data JPA 作为 ORM 方案，并以 H2 作为本地开发默认数据库。
- 连库：增加数据库连接配置，使应用可在本地启动并成功初始化数据库。
- 建表与映射：新增 1 张核心表 `echo_message`，并提供对应 Entity 与 Repository。

## Impact
- Affected specs: `data-access`
- Affected code: `pom.xml`、`src/main/resources/application.properties`、`src/main/java/com/luckin/javademo/**`（新增 entity/repository/service 等）


---
name: 最简SpringBoot服务端
overview: 生成一个最小可运行的 Spring Boot + Maven Java 服务端项目，用最少文件实现启动与一个HTTP接口，并提供最基本的配置与错误处理。
todos:
  - id: init-maven
    content: 创建 Maven 工程骨架与最小 `pom.xml`（spring-boot-starter-web）
    status: completed
  - id: boot-app
    content: 添加启动类 `Application.java` 并可启动
    status: completed
  - id: ping-api
    content: 添加最小接口 `PingController`（/ping）
    status: completed
  - id: runtime-config
    content: 添加 `application.properties` 最小配置并给出验证命令
    status: completed
---

# 最简 Spring Boot 服务端（Maven）拆解计划

## 目标

- 产出一个可直接 `mvn spring-boot:run` 启动的项目
- 提供 1 个最小接口（例如 `/ping`）验证服务可用
- 保持文件数量与概念最少（不引入数据库、不引入安全框架）

## 项目骨架（最少文件集）

- Maven：`pom.xml`
- 启动类：`src/main/java/.../Application.java`
- 控制器：`src/main/java/.../PingController.java`
- 配置：`src/main/resources/application.properties`

## 实现步骤

- 创建 Maven 工程与 `pom.xml`：仅引入 `spring-boot-starter-web`（最小 HTTP 服务能力）
- 编写 `Application` 启动类：确保本地可启动
- 编写 `PingController`：提供 GET 接口返回纯文本或 JSON
- 增加最小配置：端口、应用名（可选），保证默认即可运行
- 提供最小运行验证命令：本地启动并用 `curl` 验证接口
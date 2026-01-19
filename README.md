# java-demo

## 核心功能

- `GET /ping`：返回 `pong`
  - 访问地址：`http://localhost:8080/ping`
- `GET /ping-json`：返回 `{ "message": "pong" }`
  - 访问地址：`http://localhost:8080/ping-json`
- `POST /echo`：JSON 请求/响应示例；成功返回 `200`，`message` 缺失/为空/空白返回 `400`，非 JSON 请求返回 `415`
  - 访问命令：`curl -X POST http://localhost:8080/echo -H "Content-Type: application/json" -d '{"message":"hello"}'`
- EchoMessage CRUD（Day9）：
  - 说明：成功响应统一返回结构 `{"code":"OK","message":"OK","traceId":"...","data":...}`；资源不存在返回 `404` 且错误码 `code=NOT_FOUND`
  - 新增（Create）：`curl -X POST http://localhost:8080/echo-messages -H "Content-Type: application/json" -d '{"message":"hi"}'`
  - 查询（Read）：`curl -X GET http://localhost:8080/echo-messages/1`
  - 更新（Update）：`curl -X PUT http://localhost:8080/echo-messages/1 -H "Content-Type: application/json" -d '{"message":"hello"}'`
  - 删除（Delete）：`curl -X DELETE http://localhost:8080/echo-messages/1`
- EchoMessage 列表查询（Day10，分页/排序/条件过滤）：
  - 默认查询：`curl -X GET "http://localhost:8080/echo-messages"`
  - 分页查询：`curl -X GET "http://localhost:8080/echo-messages?page=2&size=10"`
  - 排序查询：`curl -X GET "http://localhost:8080/echo-messages?sort=createdAt&order=desc"`
  - 关键字过滤：`curl -X GET "http://localhost:8080/echo-messages?message=hi"`
  - 时间范围过滤：`curl -X GET "http://localhost:8080/echo-messages?createdAtFrom=2026-01-01T00:00:00Z&createdAtTo=2026-02-01T00:00:00Z"`
  - 非法参数示例（应返回 400）：`curl -X GET "http://localhost:8080/echo-messages?page=0"` 或 `curl -X GET "http://localhost:8080/echo-messages?size=0"`
- 本地数据库（H2，默认）：
  - 调用 `POST /echo` 时会写入 `echo_message` 表（字段：`id/message/length/created_at`），用于演示 Day7/Day8 的连库与实体映射
  
- `GET /weather?city=城市名`：查询城市天气（高德接口），返回天气、温度与穿衣建议
  - 访问地址：`http://localhost:8080/weather?city=北京`
- 统一错误响应（JSON）：发生 4xx/5xx 时返回包含 `code/message/traceId/path/timestamp` 的结构；参数校验失败时额外返回 `errors`
- traceId（请求链路标识）：
  - 支持请求头 `X-Request-Id` 透传；未传入时服务端生成
  - 响应头会回传 `X-Request-Id`，错误响应体的 `traceId` 与其一致

## 目录结构

- `pom.xml`：Maven 工程配置（Spring Boot 3.2.1，Java 17）
- `src/main/java/com/luckin/javademo/`
  - `Application.java`：应用入口
  - `EchoController.java`：回显示例接口（请求体/响应体/状态码）
  - `PingController.java`：连通性接口
  - `WeatherController.java`：天气接口
  - `service/`
    - `EchoService.java`：Echo 业务逻辑（回显与长度计算）
    - `PingService.java`：Ping 业务逻辑（返回 pong / pong-json）
    - `WeatherService.java`：天气查询业务编排（城市->adcode->天气->穿衣建议）
  - `weather/`
    - `AmapClient.java`：高德接口客户端
    - `AmapGeocodeResponse.java`：地理编码响应模型
    - `AmapLiveWeatherResponse.java`：实况天气响应模型
    - `ClothingAdvisor.java`：穿衣建议逻辑
- `src/main/resources/application.properties`：应用配置

## 环境依赖

- JDK 17
- Maven（建议 3.8+）
- 需要可访问高德开放平台接口的网络环境

## 配置项

- `server.port`：服务端口（默认 `8080`）
- `AMAP_KEY`：高德 Web 服务 Key（天气接口需要；通过环境变量注入，应用内对应 `amap.key=${AMAP_KEY:}`）

## 运行命令

- 本地启动（推荐）：
  - `mvn -q -DskipTests package`
  - `AMAP_KEY=你的高德Key mvn -q spring-boot:run`
- 端口被占用时（示例改为 18080）：
  - `AMAP_KEY=你的高德Key mvn -q spring-boot:run -Dspring-boot.run.arguments="--server.port=18080"`
- 打包并运行（jar）：
  - `mvn -q -DskipTests package`
  - `AMAP_KEY=你的高德Key java -jar target/java-demo-0.0.1-SNAPSHOT.jar`
  - `AMAP_KEY=你的高德Key java -jar target/java-demo-0.0.1-SNAPSHOT.jar --server.port=18080`
  - `90c9304458c94344bb096a8814ccf587`

## 本地验证：echo_message 落库（H2 / MySQL）

### H2（默认）

- 启动应用：`AMAP_KEY=你的高德Key mvn -q spring-boot:run`
- 调用接口：`curl -X POST http://localhost:8080/echo -H "Content-Type: application/json" -d '{"message":"hello"}'`
- 打开控制台：`http://localhost:8080/h2-console`
  - JDBC URL：`jdbc:h2:mem:java_demo`
  - User Name：`sa`
  - Password：（留空）
- 查询验证：`select * from echo_message order by id desc;`

> 常见报错：`Database "/Users/xxx/test" not found ... [90149-224]`
>
> - 原因：H2 Console 默认会填 `jdbc:h2:~/test`（文件库），本工程使用的是内存库
> - 处理：在登录页把 JDBC URL 改为 `jdbc:h2:mem:java_demo`（并确保应用已启动），再 Connect

### MySQL（profile=mysql）

- 准备数据库（示例）：创建库 `java_demo`（并确保账号有权限）
- 启动应用（启用 profile=mysql）：
  - `AMAP_KEY=你的高德Key MYSQL_URL="jdbc:mysql://localhost:3306/java_demo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai" MYSQL_USERNAME=root MYSQL_PASSWORD=你的密码 mvn -q spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"`
- 调用接口：`curl -X POST http://localhost:8080/echo -H "Content-Type: application/json" -d '{"message":"hello"}'`
- 查询验证（使用 mysql 客户端）：`mysql -h localhost -u root -p -D java_demo -e "select * from echo_message order by id desc;"`
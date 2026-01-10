# java-demo

## 核心功能

- `GET /ping`：返回 `pong`
  - 访问地址：`http://localhost:8080/ping`
- `GET /ping-json`：返回 `{ "message": "pong" }`
  - 访问地址：`http://localhost:8080/ping-json`
- `POST /echo`：JSON 请求/响应示例；成功返回 `200`，`message` 缺失/为空/空白返回 `400`，非 JSON 请求返回 `415`
  - 访问命令：`curl -X POST http://localhost:8080/echo -H "Content-Type: application/json" -d '{"message":"hello"}'`
  
- `GET /weather?city=城市名`：查询城市天气（高德接口），返回天气、温度与穿衣建议
  - 访问地址：`http://localhost:8080/weather?city=北京`

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
- `amap.key`：高德 Web 服务 Key（天气接口需要）

## 运行命令

- 本地启动：
  - `mvn spring-boot:run`
- 打包并运行：
  - `mvn clean package`
  - `java -jar target/java-demo-0.0.1-SNAPSHOT.jar`


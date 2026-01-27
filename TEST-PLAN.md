# Java-Demo 项目测试计划

## 📊 项目现状分析

### 当前测试状态
- ❌ **测试目录**: 不存在 (`src/test/`)
- ❌ **测试文件**: 0 个
- ❌ **测试依赖**: 未配置
- ✅ **代码结构**: 清晰的分层架构，适合测试

### 项目规模
- **Java 源文件**: 24 个
- **代码总行数**: ~1,453 行
- **Service 层**: 4 个服务 (662 行)
- **Controller 层**: 4 个控制器 (349 行)
- **其他组件**: 16 个支持类

---

## 🎯 测试策略

### 测试金字塔原则

```
        /\
       /  \   集成测试 (~15 个)
      /    \
     /------\ Service 单元测试 (~20 个)
    /        \
   /----------\
```

### 优先级划分

| 优先级 | 组件类型 | 测试数量 | 理由 |
|--------|---------|---------|------|
| ⭐⭐⭐ **P0** | Service 层 | ~20 | 业务核心，影响范围大 |
| ⭐⭐⭐ **P0** | Controller 层 | ~15 | API 契约，用户入口 |
| ⭐⭐ **P1** | 异常处理 | ~6 | 错误路径必须覆盖 |
| ⭐⭐ **P1** | 外部客户端 | ~3 | Mock 外部依赖 |
| ⭐ **P2** | 过滤器 | ~4 | 辅助功能 |

---

## 📋 详细测试列表

### 1. Service 层单元测试 (20 个测试用例)

#### 1.1 PingServiceTest (2 个)
```
✅ ping() - 应返回 "pong"
✅ pingJson() - 应返回包含 "message": "pong" 的 Map
```

#### 1.2 EchoServiceTest (3 个)
```
✅ echo("hello") - 应返回正确的结果
✅ echo("hello") - 应正确保存到数据库
✅ 边界条件: 空/空白/长字符串处理
```

#### 1.3 EchoMessageCrudServiceTest (8 个)
```
✅ create() - 成功创建并返回完整结果
✅ create() - 数据库应包含新记录
✅ getById() - 返回正确的记录
✅ getById() - 不存在的 ID 应抛出 ResourceNotFoundException
✅ updateById() - 成功更新记录
✅ updateById() - 不存在的 ID 应抛出异常
✅ deleteById() - 成功删除记录
✅ deleteById() - 不存在的 ID 应抛出异常
```

#### 1.4 EchoMessageCrudServiceListTest (5 个)
```
✅ list() - 默认查询应返回分页结果
✅ list() - 分页参数应正确工作
✅ list() - 排序功能应正确工作
✅ list() - message 过滤应正确工作
✅ list() - 时间范围过滤应正确工作
```

#### 1.5 WeatherServiceTest (3 个)
```
✅ queryByCity("北京") - 成功返回天气和穿衣建议
✅ queryByCity("不存在的城市") - 应处理异常
✅ 边界条件: 温度解析失败应返回 null
```

#### 1.6 ClothingAdvisorTest (5 个)
```
✅ advise(-5, "晴") - 应返回厚羽绒服建议
✅ advise(15, "晴") - 应返回适中建议
✅ advise(30, "晴") - 应返回短袖短裤建议
✅ advise(20, "小雨") - 应包含雨具建议
✅ advise(10, "雪") - 应包含防滑保暖建议
```

---

### 2. Controller 层集成测试 (15 个测试用例)

#### 2.1 PingControllerTest (2 个)
```
✅ GET /ping - 应返回 "pong" 和状态码 200
✅ GET /ping-json - 应返回 JSON 格式响应
```

#### 2.2 EchoControllerTest (4 个)
```
✅ POST /echo (成功) - 应返回正确响应和状态码 200
✅ POST /echo (空 message) - 应返回 400 和错误信息
✅ POST /echo (非 JSON) - 应返回 415
✅ POST /echo (空白 message) - 应返回 400
```

#### 2.3 EchoMessageControllerTest (8 个)
```
✅ POST /echo-messages - 成功创建并返回完整响应
✅ GET /echo-messages/1 - 返回正确的记录
✅ GET /echo-messages/999 - 不存在应返回 404
✅ PUT /echo-messages/1 - 成功更新
✅ PUT /echo-messages/999 - 不存在应返回 404
✅ DELETE /echo-messages/1 - 成功删除
✅ DELETE /echo-messages/999 - 不存在应返回 404
✅ GET /echo-messages (列表查询) - 分页和过滤应正确工作
```

#### 2.4 WeatherControllerTest (3 个)
```
✅ GET /weather?city=北京 - 成功返回天气信息
✅ GET /weather (缺失 city) - 应返回 400
✅ GET /weather?city=空城市 - 应处理异常
```

---

### 3. 异常处理测试 (6 个测试用例)

#### GlobalExceptionHandlerTest
```
✅ MethodArgumentNotValidException - 应返回 400 和字段错误
✅ BindException - 应返回 400 和字段错误
✅ HttpMessageNotReadableException - 应返回 400
✅ MissingServletRequestParameterException - 应返回 400
✅ ResponseStatusException (4xx) - 应返回正确的状态码
✅ ResponseStatusException (5xx) - 应返回 "系统繁忙"
```

---

### 4. 外部 API Mock 测试 (3 个测试用例)

#### AmapClientTest
```
✅ geocodeToAdcode("北京") - 应返回正确的 adcode
✅ geocodeToAdcode("不存在的城市") - 应抛出异常
✅ liveWeather("110000") - 应返回天气信息
```

---

### 5. 过滤器测试 (4 个测试用例)

#### TraceIdFilterTest
```
✅ 无 X-Request-Id 头 - 应生成新的 traceId
✅ 有 X-Request-Id 头 - 应透传该 ID
✅ traceId 应在响应头中返回
✅ traceId 应在日志 MDC 中设置
```

---

## 🔧 环境配置

### 步骤 1: 添加测试依赖

在 `pom.xml` 的 `<dependencies>` 节点中添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- 可选: 代码覆盖率工具 -->
<dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</dependency>
```

### 步骤 2: 创建测试目录结构

```
src/test/java/com/luckin/javademo/
├── service/
│   ├── PingServiceTest.java
│   ├── EchoServiceTest.java
│   ├── EchoMessageCrudServiceTest.java
│   ├── WeatherServiceTest.java
│   └── weather/
│       └── ClothingAdvisorTest.java
├── controller/
│   ├── PingControllerTest.java
│   ├── EchoControllerTest.java
│   ├── EchoMessageControllerTest.java
│   └── WeatherControllerTest.java
├── error/
│   └── GlobalExceptionHandlerTest.java
├── weather/
│   └── AmapClientTest.java
└── logging/
    └── TraceIdFilterTest.java
```

### 步骤 3: 创建测试资源文件（可选）

```
src/test/resources/
├── application-test.properties  # 测试专用配置
└── data/                        # 测试数据文件（如果需要）
```

---

## 📝 测试代码示例

### 示例 1: Service 单元测试

```java
package com.luckin.javademo.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PingServiceTest {

    @Test
    void ping_shouldReturnPong() {
        PingService service = new PingService();
        String result = service.ping();
        assertEquals("pong", result);
    }

    @Test
    void pingJson_shouldReturnMapWithPongMessage() {
        PingService service = new PingService();
        var result = service.pingJson();
        assertEquals("pong", result.get("message"));
    }
}
```

### 示例 2: Service 集成测试（带数据库）

```java
package com.luckin.javademo.service;

import com.luckin.javademo.persistence.EchoMessageEntity;
import com.luckin.javademo.persistence.EchoMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EchoMessageCrudServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EchoMessageRepository repository;

    private EchoMessageCrudService service;

    @BeforeEach
    void setUp() {
        service = new EchoMessageCrudService(repository);
    }

    @Test
    void create_shouldPersistAndReturnResult() {
        EchoMessageCrudService.EchoMessageResult result = service.create("hello");

        assertNotNull(result.id());
        assertEquals("hello", result.message());
        assertEquals(5, result.length());

        // 验证数据库中存在
        EchoMessageEntity entity = entityManager.find(EchoMessageEntity.class, result.id());
        assertNotNull(entity);
        assertEquals("hello", entity.getMessage());
    }
}
```

### 示例 3: Controller 集成测试

```java
package com.luckin.javademo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EchoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void echo_withValidMessage_shouldReturn200() throws Exception {
        mockMvc.perform(post("/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("hello"))
            .andExpect(jsonPath("$.length").value(5));
    }

    @Test
    void echo_withEmptyMessage_shouldReturn400() throws Exception {
        mockMvc.perform(post("/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"\"}"))
            .andExpect(status().isBadRequest());
    }
}
```

### 示例 4: Mock 外部 API

```java
package com.luckin.javademo.weather;

import com.luckin.javademo.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private AmapClient amapClient;

    @Test
    void queryByCity_shouldReturnWeatherResult() {
        // Arrange
        AmapLiveWeatherResponse.Live mockLive = new AmapLiveWeatherResponse.Live("晴", "20", "20240127120000");
        when(amapClient.geocodeToAdcode("北京")).thenReturn("110000");
        when(amapClient.liveWeather("110000")).thenReturn(mockLive);

        // Act
        WeatherService.WeatherResult result = weatherService.queryByCity("北京");

        // Assert
        assertEquals("晴", result.weather());
        assertEquals(20, result.temperature());
        assertNotNull(result.clothingAdvice());
    }
}
```

---

## 🚀 测试运行指南

### 基本命令

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=PingServiceTest

# 运行单个测试方法
mvn test -Dtest=PingServiceTest#ping_shouldReturnPong

# 运行特定包下的测试
mvn test -Dtest=com.luckin.javademo.service.*

# 跳过测试
mvn package -DskipTests

# 运行测试并生成报告
mvn test surefire-report:report
```

### 覆盖率报告（使用 JaCoCo）

```bash
# 生成覆盖率报告
mvn test jacoco:report

# 查看报告（浏览器打开）
open target/site/jacoco/index.html
```

### IDE 中运行

**IntelliJ IDEA:**
1. 右键点击测试类或方法
2. 选择 "Run" 或 "Debug"
3. 查看测试结果面板

**VS Code:**
1. 安装 "Java Test Runner" 扩展
2. 点击测试类左侧的绿色运行按钮

---

## 📊 测试覆盖率目标

### 整体目标
| 指标 | 目标值 | 最低要求 |
|------|--------|---------|
| 整体覆盖率 | ≥ 75% | ≥ 65% |
| Service 层 | ≥ 85% | ≥ 80% |
| Controller 层 | ≥ 70% | ≥ 60% |
| GlobalExceptionHandler | ≥ 90% | ≥ 85% |

### 各模块详细目标

| 模块 | 行覆盖率 | 分支覆盖率 |
|------|---------|-----------|
| PingService | 100% | N/A |
| EchoService | ≥ 90% | ≥ 80% |
| EchoMessageCrudService | ≥ 85% | ≥ 75% |
| WeatherService | ≥ 80% | ≥ 70% |
| ClothingAdvisor | ≥ 95% | ≥ 90% |
| PingController | ≥ 70% | ≥ 60% |
| EchoController | ≥ 70% | ≥ 60% |
| EchoMessageController | ≥ 65% | ≥ 55% |
| WeatherController | ≥ 60% | ≥ 50% |
| GlobalExceptionHandler | ≥ 90% | ≥ 85% |
| AmapClient | ≥ 70% | ≥ 60% |
| TraceIdFilter | ≥ 60% | ≥ 50% |

---

## ⏱️ 实施时间表

### 阶段 1: 基础设施搭建 (1-2 小时)
- [ ] 添加测试依赖到 pom.xml
- [ ] 创建测试目录结构
- [ ] 配置测试环境（application-test.properties）
- [ ] 验证测试框架可用

### 阶段 2: Service 层测试 (3-4 小时)
- [ ] PingServiceTest (30 分钟)
- [ ] EchoServiceTest (30 分钟)
- [ ] EchoMessageCrudServiceTest (1 小时)
- [ ] WeatherServiceTest (30 分钟)
- [ ] ClothingAdvisorTest (30 分钟)

### 阶段 3: Controller 层测试 (3-4 小时)
- [ ] PingControllerTest (30 分钟)
- [ ] EchoControllerTest (1 小时)
- [ ] EchoMessageControllerTest (1.5 小时)
- [ ] WeatherControllerTest (30 分钟)

### 阶段 4: 异常处理和 Mock 测试 (2-3 小时)
- [ ] GlobalExceptionHandlerTest (1.5 小时)
- [ ] AmapClientTest (1 小时)

### 阶段 5: 配置覆盖率报告 (1 小时)
- [ ] 添加 JaCoCo 插件
- [ ] 配置覆盖率阈值
- [ ] 生成初始覆盖率报告
- [ ] 调整测试以达到目标

### 阶段 6: CI/CD 集成 (可选, 1-2 小时)
- [ ] 配置 GitHub Actions 工作流
- [ ] 自动运行测试
- [ ] 自动生成覆盖率报告
- [ ] 设置 PR 检查规则

**总计时间估计**: 10-16 小时（根据熟练度）

---

## 🎓 最佳实践建议

### 1. 测试命名规范
```java
// ✅ 好的命名
void create_withValidMessage_shouldReturn200()
void getById_withNonExistingId_shouldReturn404()
void list_withPagination_shouldReturnCorrectPage()

// ❌ 避免
void testCreate()
void testGetById()
```

### 2. AAA 模式 (Arrange-Act-Assert)
```java
@Test
void updateById_withValidData_shouldUpdateRecord() {
    // Arrange: 准备测试数据
    EchoMessageEntity existing = createTestMessage();

    // Act: 执行被测方法
    EchoMessageCrudService.EchoMessageResult result = service.updateById(existing.getId(), "updated");

    // Assert: 验证结果
    assertEquals("updated", result.message());
    assertEquals(7, result.length());
}
```

### 3. 测试独立性
- 每个测试应该独立运行
- 不依赖其他测试的执行顺序
- 使用 `@BeforeEach` 清理或重置状态

### 4. Mock 的合理使用
```java
// ✅ 适当: Mock 外部依赖
@MockBean
private AmapClient amapClient;

// ❌ 不当: Mock 内部逻辑
@MockBean
private EchoService echoService; // 应该测试实际逻辑
```

### 5. 边界条件测试
```java
@Test
void create_withMaxLengthMessage_shouldSucceed() {
    String longMessage = "a".repeat(1024); // 最大长度
    EchoMessageCrudService.EchoMessageResult result = service.create(longMessage);
    assertNotNull(result);
}

@Test
void create_withEmptyMessage_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> service.create(""));
}
```

---

## 📈 持续改进

### 定期审查
- **每周**: 检查测试覆盖率变化
- **每月**: 审查失败测试，修复不稳定的测试
- **每季度**: 评估测试策略有效性

### 测试质量指标
- 测试执行时间（应 < 2 分钟）
- 测试通过率（应保持 100%）
- 代码覆盖率趋势（应上升或稳定）

---

## 📚 参考资料

### 官方文档
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

### 推荐阅读
- 《测试驱动的面向对象软件设计》
- 《单元测试的艺术》
- 《有效测试》

---

## ✅ 实施检查清单

### 准备阶段
- [ ] 确认 Java 版本 (17)
- [ ] 确认 Maven 版本 (3.8+)
- [ ] 备份当前代码
- [ ] 创建 feature 分支

### 执行阶段
- [ ] 添加测试依赖
- [ ] 创建测试目录
- [ ] 编写 Service 层测试
- [ ] 编写 Controller 层测试
- [ ] 编写异常处理测试
- [ ] 配置覆盖率工具
- [ ] 运行所有测试
- [ ] 达到覆盖率目标

### 验证阶段
- [ ] 所有测试通过
- [ ] 覆盖率达到目标
- [ ] 测试执行时间可接受
- [ ] 代码审查通过

### 部署阶段
- [ ] 合并到主分支
- [ ] CI/CD 集成（如果适用）
- [ ] 文档更新
- [ ] 团队培训

---

## 🎯 总结

这个测试计划覆盖了项目的所有核心组件，采用了测试金字塔的最佳实践，确保：

✅ **高覆盖率** - 整体达到 75%+
✅ **高效率** - 测试执行时间 < 2 分钟
✅ **高维护性** - 清晰的结构和命名
✅ **高可靠性** - 边界条件和异常路径全覆盖

**预期收益**:
- 提高代码质量
- 减少生产环境 bug
- 加快重构速度
- 增强团队信心

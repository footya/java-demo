package com.luckin.javademo;

import com.luckin.javademo.service.EchoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Echo 示例接口：
 * - 演示 Controller 与路由（POST /echo）
 * - 演示 JSON 请求体（@RequestBody）与 JSON 响应体（record 自动序列化）
 * - 演示常见状态码：200（成功）、400（参数校验失败）、415（不支持的媒体类型）
 */
@RestController
public class EchoController {
    /**
     * 依赖注入（构造器注入）：
     * - Controller 只处理协议层，把业务逻辑交给 Service
     */
    private final EchoService echoService;

    public EchoController(EchoService echoService) {
        this.echoService = echoService;
    }

    /**
     * 路由定义：
     * - value: 接口路径
     * - consumes: 声明该接口“只接收” application/json
     *   - 当客户端传入 Content-Type 不是 application/json 时，Spring 会在进入方法前拦截并返回 415
     * - produces: 声明该接口“只输出” application/json
     *   - 返回值会被 Spring 使用消息转换器序列化为 JSON，并设置响应头 Content-Type
     */
    @PostMapping(value = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    /**
     * 业务处理：
     * - @RequestBody：把请求体 JSON 反序列化为 EchoRequest
     * - 返回 EchoResponse：会被自动序列化为 JSON
     *
     * 约定（与 spec 对齐）：
     * - message 缺失 / 为 null / 为空字符串 / 仅空白 => 400
     * - 合法 message => 200 + 回显 message 与 length
     */
    public EchoResponse echo(@Valid @RequestBody EchoRequest request) {
        // 入参校验交给 Bean Validation：
        // - message 为空/空白 -> 400（由全局异常处理转换成统一错误响应）
        EchoService.EchoResult result = echoService.echo(request.message());
        // 返回值会自动成为响应体 JSON，字段名来自 record 的组件名（message/length）。
        return new EchoResponse(result.message(), result.length());
    }

    /**
     * 请求体 DTO（Data Transfer Object）：
     * - 期望客户端以 JSON 形式传入：{"message":"hi"}
     * - record 是 Java 16+ 的语法糖：自动生成构造器、getter（这里是 message()）、equals/hashCode/toString
     */
    public record EchoRequest(@NotBlank(message = "message required") String message) {}

    /**
     * 响应体 DTO：
     * - 成功时返回 JSON：{"message":"hi","length":2}
     * - length 使用 int：这里保证非空；若要允许空可改 Integer
     */
    public record EchoResponse(String message, int length) {}
}


package com.luckin.javademo;

import java.util.Map;

import com.luckin.javademo.service.PingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    /**
     * 示例：即使业务很简单，也把返回值逻辑放到 Service，
     * 让 Controller 专注协议层（路由 + 调用 + 返回）。
     */
    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping("/ping")
    public String ping() {
        return pingService.ping();
    }

    @GetMapping("/ping-json")
    public Map<String, Object> pingJson() {
        return pingService.pingJson();
    }
}



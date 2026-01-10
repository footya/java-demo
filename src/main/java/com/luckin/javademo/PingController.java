package com.luckin.javademo;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/ping-json")
    public Map<String, Object> pingJson() {
        return Map.of("message", "pong");
    }
}



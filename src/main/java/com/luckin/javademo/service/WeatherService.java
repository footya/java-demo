package com.luckin.javademo.service;

import org.springframework.stereotype.Service;

import com.luckin.javademo.weather.AmapClient;
import com.luckin.javademo.weather.AmapLiveWeatherResponse;
import com.luckin.javademo.weather.ClothingAdvisor;

/**
 * 天气业务服务：
 * - 负责把“城市 -> adcode -> 实况天气 -> 温度解析 -> 穿衣建议”串起来（业务编排）
 * - 不直接参与 HTTP 协议层的入参解析/校验与响应封装
 */
@Service
public class WeatherService {
    private final AmapClient amapClient;
    private final ClothingAdvisor clothingAdvisor;

    public WeatherService(AmapClient amapClient, ClothingAdvisor clothingAdvisor) {
        this.amapClient = amapClient;
        this.clothingAdvisor = clothingAdvisor;
    }

    /**
     * 根据城市查询天气与穿衣建议。
     *
     * 边界说明：
     * - city 的“必填/空白”校验由 Controller 负责（协议层规则，返回 400）；
     * - AmapClient 内部可能抛出 ResponseStatusException（例如 400/500/502），这里不吞掉，保持对外行为一致。
     */
    public WeatherResult queryByCity(String city) {
        String adcode = amapClient.geocodeToAdcode(city);
        AmapLiveWeatherResponse.Live live = amapClient.liveWeather(adcode);

        Integer temperature = parseIntOrNull(live.temperature());
        String clothingAdvice = clothingAdvisor.advise(temperature, live.weather());

        return new WeatherResult(live.weather(), temperature, clothingAdvice);
    }

    /**
     * 温度字段来自外部接口，存在空值/非数字的可能：
     * - 空或空白 => 返回 null
     * - 非数字 => 返回 null（避免把上游脏数据当成业务异常）
     */
    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Service 层返回的业务结果对象：
     * - 与 Controller 的响应 DTO 解耦，便于后续在不同协议（HTTP/消息）间复用业务
     */
    public record WeatherResult(String weather, Integer temperature, String clothingAdvice) {}
}


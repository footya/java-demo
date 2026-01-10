package com.luckin.javademo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.luckin.javademo.service.WeatherService;

@RestController
public class WeatherController {
    /**
     * Controller 只做协议层：入参解析/校验与响应封装。
     * 天气查询的编排逻辑下沉到 Service。
     */
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public WeatherResponse weather(@RequestParam("city") String city) {
        if (city == null || city.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "city required");
        }

        WeatherService.WeatherResult result = weatherService.queryByCity(city);
        return new WeatherResponse(result.weather(), result.temperature(), result.clothingAdvice());
    }

    public record WeatherResponse(String weather, Integer temperature, String clothingAdvice) {}
}



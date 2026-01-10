package com.luckin.javademo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.luckin.javademo.weather.AmapClient;
import com.luckin.javademo.weather.AmapLiveWeatherResponse;
import com.luckin.javademo.weather.ClothingAdvisor;

@RestController
public class WeatherController {
    private final AmapClient amapClient;
    private final ClothingAdvisor clothingAdvisor;

    public WeatherController(AmapClient amapClient, ClothingAdvisor clothingAdvisor) {
        this.amapClient = amapClient;
        this.clothingAdvisor = clothingAdvisor;
    }

    @GetMapping("/weather")
    public WeatherResponse weather(@RequestParam("city") String city) {
        if (city == null || city.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "city required");
        }

        String adcode = amapClient.geocodeToAdcode(city);
        AmapLiveWeatherResponse.Live live = amapClient.liveWeather(adcode);

        Integer temperature = parseIntOrNull(live.temperature());
        String clothingAdvice = clothingAdvisor.advise(temperature, live.weather());

        return new WeatherResponse(live.weather(), temperature, clothingAdvice);
    }

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

    public record WeatherResponse(String weather, Integer temperature, String clothingAdvice) {}
}



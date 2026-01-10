package com.luckin.javademo.weather;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AmapClient {
    private final RestClient restClient;
    private final String key;

    public AmapClient(@Value("${amap.key:}") String key) {
        this.restClient = RestClient.builder().baseUrl("https://restapi.amap.com").build();
        this.key = key;
    }

    public String geocodeToAdcode(String cityName) {
        requireKey();

        AmapGeocodeResponse resp = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v3/geocode/geo")
                        .queryParam("address", cityName)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(502), "amap geocode upstream error");
                })
                .body(AmapGeocodeResponse.class);

        if (resp == null || !"1".equals(resp.status())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(502), "amap geocode upstream error");
        }
        List<AmapGeocodeResponse.Geocode> geocodes = resp.geocodes();
        if (geocodes == null || geocodes.isEmpty() || geocodes.get(0).adcode() == null || geocodes.get(0).adcode().isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "city not found");
        }
        return geocodes.get(0).adcode();
    }

    public AmapLiveWeatherResponse.Live liveWeather(String adcode) {
        requireKey();

        AmapLiveWeatherResponse resp = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v3/weather/weatherInfo")
                        .queryParam("city", adcode)
                        .queryParam("extensions", "base")
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(502), "amap weather upstream error");
                })
                .body(AmapLiveWeatherResponse.class);

        if (resp == null || !"1".equals(resp.status())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(502), "amap weather upstream error");
        }
        List<AmapLiveWeatherResponse.Live> lives = resp.lives();
        if (lives == null || lives.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(502), "amap weather upstream error");
        }
        return lives.get(0);
    }

    private void requireKey() {
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "amap.key not configured");
        }
    }
}



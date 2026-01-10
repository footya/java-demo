package com.luckin.javademo.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmapLiveWeatherResponse(String status, List<Live> lives) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Live(String weather, String temperature, @JsonProperty("reporttime") String reportTime) {}
}



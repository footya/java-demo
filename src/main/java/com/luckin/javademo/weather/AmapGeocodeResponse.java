package com.luckin.javademo.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmapGeocodeResponse(String status, List<Geocode> geocodes) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geocode(String adcode) {}
}



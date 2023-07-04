package com.skyapi.weatherforecast.hourly.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeatherListDTO {
    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> hourlyForecast = new ArrayList<>();

    public void addWeatherHourlyDTO(HourlyWeatherDTO dto) {
        this.hourlyForecast.add(dto);
    }
}

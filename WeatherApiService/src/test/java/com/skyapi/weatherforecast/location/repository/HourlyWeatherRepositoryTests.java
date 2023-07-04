package com.skyapi.weatherforecast.location.repository;

import com.skyapi.weatherforecast.base.BaseRepositoryTests;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.repository.HourlyWeatherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HourlyWeatherRepositoryTests extends BaseRepositoryTests {

    @Autowired
    private HourlyWeatherRepository repo;

    @Test
    public void testAdd() {
        String locationCode = "NYC_USA";
        int hourOfDay = 12;

        Location location = new Location().code(locationCode);

        HourlyWeather forecast = new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather updatedForecast = repo.save(forecast);

        assertEquals(updatedForecast.getId().getLocation().getCode(),locationCode);
        assertEquals(updatedForecast.getId().getHourOfDay(),hourOfDay);

    }

    @Test
    public void testDelete() {

        String locationCode = "NYC_USA";
        Location location = new Location().code(locationCode);

        int hourOfDay = 12;
        HourlyWeatherId id = new HourlyWeatherId(hourOfDay, location);

        repo.deleteById(id);

        Optional<HourlyWeather> result = repo.findById(id);
        assertThat(result).isNotPresent();
    }

    @Test
    public void testFindByLocationCodeFound() {
        String locationCode = "DELHI_IN";
        int currentHour = 10;

        List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecast).isNotEmpty();
    }

    @Test
    public void testFindByLocationCodeNotFound() {
        String locationCode = "MBMH_IN";
        int currentHour = 6;

        List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecast).isEmpty();
    }
}

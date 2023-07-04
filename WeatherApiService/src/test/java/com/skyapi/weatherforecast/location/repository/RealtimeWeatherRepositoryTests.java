package com.skyapi.weatherforecast.location.repository;

import com.skyapi.weatherforecast.base.BaseRepositoryTests;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.realtime.repository.RealtimeWeatherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RealtimeWeatherRepositoryTests extends BaseRepositoryTests {

    @Autowired
    private RealtimeWeatherRepository repo;

    @Test
    public void testUpdateForRealtimeWeather() {
        String locationCode = "NYC_USA";

        // given
        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        RealtimeWeather realtimeWeatherSample = RealtimeWeather.builder()
                .location(location)
                .build();

        repo.save(realtimeWeatherSample);

        RealtimeWeather realtimeWeather = repo.findById(locationCode).get();

        RealtimeWeather updatedRealtimeWeather = realtimeWeather.toBuilder()
                .temperature(-2)
                .humidity(32)
                .precipitation(42)
                .status("Snowy")
                .windSpeed(12)
                .lastUpdated(LocalDateTime.of(2023, 3, 2, 15, 30))
                .build();

        // when
        updatedRealtimeWeather = repo.save(updatedRealtimeWeather);

        // then
        assertEquals(updatedRealtimeWeather.getHumidity(), 32);
        assertEquals(updatedRealtimeWeather.getTemperature(), -2);
        assertEquals(updatedRealtimeWeather.getPrecipitation(), 42);
        assertEquals(updatedRealtimeWeather.getStatus(), "Snowy");
        assertEquals(updatedRealtimeWeather.getWindSpeed(), 12);
        assertEquals(updatedRealtimeWeather.getLastUpdated(), LocalDateTime.of(2023, 3, 2, 15, 30));
    }

    @Test
    public void testFindByCountryCodeAndCityNotFound() {
        // given
        String countryCode = "JP";
        String cityName = "Tokyo";

        // when
        RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);

        // then
        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityFound() {
        // given
        String countryCode = "US";
        String cityName = "New York City";

        // when
        RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);

        // then
        assertThat(realtimeWeather).isNotNull();
        assertEquals(realtimeWeather.getLocation().getCityName(), cityName);
    }

    @Test
    public void testFindByLocationNotFound() {
        String locationCode = "ABCXYZ";
        RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);

        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByTrashedLocationNotFound() {
        String locationCode = "NYC_USA";
        RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);

        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByLocationFound() {
        String locationCode = "DELHI_IN";
        RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);

        assertThat(realtimeWeather).isNotNull();
        assertThat(realtimeWeather.getLocationCode()).isEqualTo(locationCode);
    }

}

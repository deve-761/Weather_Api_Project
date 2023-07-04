package com.skyapi.weatherforecast.location.service;

import com.skyapi.weatherforecast.base.BaseServiceTest;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.repository.LocationRepository;
import com.skyapi.weatherforecast.realtime.repository.RealtimeWeatherRepository;
import com.skyapi.weatherforecast.realtime.service.RealtimeWeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class RealTimeWeatherServiceTests extends BaseServiceTest {

    @Mock
    private RealtimeWeatherRepository realtimeWeatherRepo;

    @Mock
    private LocationRepository locationRepo;

    @InjectMocks
    private RealtimeWeatherService realtimeWeatherService;

    @Test
    public void testGetByLocation() throws LocationNotFoundException {

        // given
        String locationCode = "NYC_USA";

        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealtimeWeather realtimeWeather = RealtimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(LocalDateTime.of(2023, 3, 2, 15, 30))
                .precipitation(88)
                .status("Cloudy")
                .windSpeed(5)
                .build();

        // when
        when(realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName)).thenReturn(realtimeWeather);

        RealtimeWeather realtimeWeatherInfo = realtimeWeatherService.getByLocation(location);

        // then
        assertEquals(realtimeWeatherInfo.getLastUpdated(),realtimeWeatherInfo.getLastUpdated());
        assertEquals(realtimeWeatherInfo.getStatus(),realtimeWeatherInfo.getStatus());
        assertEquals(realtimeWeatherInfo.getTemperature(),realtimeWeatherInfo.getTemperature());
        assertEquals(realtimeWeatherInfo.getWindSpeed(),realtimeWeatherInfo.getWindSpeed());

    }

    @Test
    public void testGetByLocationCode() throws LocationNotFoundException {
        // given
        String locationCode = "NYC_USA";

        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        RealtimeWeather realtimeWeather = RealtimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(LocalDateTime.of(2023, 3, 2, 15, 30))
                .precipitation(88)
                .status("Cloudy")
                .windSpeed(5)
                .build();

        // when
        when(realtimeWeatherRepo.findByLocationCode(locationCode)).thenReturn(realtimeWeather);

        RealtimeWeather realtimeWeatherInfo = realtimeWeatherService.getByLocationCode(locationCode);

        // then
        assertEquals(realtimeWeatherInfo.getLastUpdated(),realtimeWeatherInfo.getLastUpdated());
        assertEquals(realtimeWeatherInfo.getStatus(),realtimeWeatherInfo.getStatus());
        assertEquals(realtimeWeatherInfo.getTemperature(),realtimeWeatherInfo.getTemperature());
        assertEquals(realtimeWeatherInfo.getWindSpeed(),realtimeWeatherInfo.getWindSpeed());
    }

    @Test
    public void testUpdateNewRealtimeWeather() throws LocationNotFoundException {

        // given
        String locationCode = "NYC_USA";

        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        RealtimeWeather realtimeWeather = RealtimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(LocalDateTime.of(2023, 3, 2, 15, 30))
                .precipitation(88)
                .status("Cloudy")
                .windSpeed(5)
                .build();

        // when
        when(locationRepo.findByCode(locationCode)).thenReturn(location);

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        when(locationRepo.findByCode(locationCode)).thenReturn(location);
        when(realtimeWeatherRepo.save(realtimeWeather)).thenReturn(realtimeWeather);

        RealtimeWeather updatedRealtimeWeatherInfo = location.getRealtimeWeather();

        RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeather);

        // then
        assertNotNull(updatedRealtimeWeather);
        assertEquals(updatedRealtimeWeather.getLastUpdated(),updatedRealtimeWeatherInfo.getLastUpdated());
        assertEquals(updatedRealtimeWeather.getStatus(),updatedRealtimeWeatherInfo.getStatus());
        assertEquals(updatedRealtimeWeather.getTemperature(),updatedRealtimeWeatherInfo.getTemperature());
        assertEquals(updatedRealtimeWeather.getWindSpeed(),updatedRealtimeWeatherInfo.getWindSpeed());
        assertEquals(location, updatedRealtimeWeather.getLocation());
    }
}

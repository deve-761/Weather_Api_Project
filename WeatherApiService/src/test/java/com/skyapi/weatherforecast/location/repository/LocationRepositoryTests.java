package com.skyapi.weatherforecast.location.repository;

import com.skyapi.weatherforecast.base.BaseRepositoryTests;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationRepositoryTests extends BaseRepositoryTests {

    @Autowired
    private LocationRepository repository;

    @Test
    public void testAddLocationSuccess() {

        // given
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        // when
        Location savedLocation = repository.save(location);

        // then
        assertThat(savedLocation).isNotNull();
        assertEquals(savedLocation.getCode(), "NYC_USA");
    }

    @Test
    public void testLocationListSuccess() {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        // when
        repository.save(location);
        List<Location> locations = repository.findUntrashed();

        // then
        assertThat(locations).isNotEmpty();
        locations.forEach(System.out::println);
    }

    @Test
    public void testGetLocationNotFound() {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String code = "ABCD";

        // when
        repository.save(location);
        Location findlocationByCode = repository.findByCode(code);

        // then
        assertThat(findlocationByCode).isNull();
    }

    @Test
    public void testGetLocationFound() {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String code = "NYC_USA";

        // when
        repository.save(location);
        Location findlocationByCode = repository.findByCode(code);

        // then
        assertThat(findlocationByCode).isNotNull();
        assertEquals(findlocationByCode.getCode(), code);
    }

    @Test
    public void testUpdateLocationSuccess() {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .trashed(false)
                .build();

        String code = "NYC_USA";

        // when
        repository.save(location);

        Location findlocationByCode = repository.findByCode(code);

        findlocationByCode.setCityName("New York City Updated");
        findlocationByCode.setRegionName("New York Updated");
        findlocationByCode.setCountryCode("US");
        findlocationByCode.setCountryName("United States of America Updated");
        findlocationByCode.setEnabled(true);
        findlocationByCode.setTrashed(false);

        // when
        repository.save(findlocationByCode);

        Location updatedLocation = repository.findByCode(code);

        // then
        assertThat(updatedLocation).isNotNull();
        assertEquals(updatedLocation.getCode(), "NYC_USA");
        assertEquals(updatedLocation.getCityName(), "New York City Updated");
        assertEquals(updatedLocation.getRegionName(), "New York Updated");
        assertEquals(updatedLocation.getCountryCode(), "US");
        assertEquals(updatedLocation.getCountryName(), "United States of America Updated");
        assertEquals(updatedLocation.isEnabled(), true);

    }


    @Test
    public void testTrashLocationSuccess() {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .trashed(false)
                .build();

        // when
        repository.save(location);

        String code = "NYC_USA";
        repository.trashByCode(code);

        Location deletedLocation = repository.findByCode(code);

        // then
        assertThat(deletedLocation).isNull();
    }

    @Test
    public void testAddRealtimeWeatherData() {
        String code = "NYC_USA";

        // given
        Location location = Location.builder()
                .code(code)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .trashed(false)
                .realtimeWeather(null)
                .build();

        repository.save(location);

        Location getlocation = repository.findByCode(code);

        RealtimeWeather realtimeWeather = getlocation.getRealtimeWeather();

        if (realtimeWeather == null) {
            realtimeWeather = RealtimeWeather.builder()
                    .location(getlocation)
                    .build();
            getlocation.setRealtimeWeather(realtimeWeather);
        }

        realtimeWeather = realtimeWeather.toBuilder()
                .temperature(-1)
                .humidity(30)
                .precipitation(40)
                .status("Snowy")
                .windSpeed(15)
                .lastUpdated(LocalDateTime.of(2023, 3, 2, 15, 30))
                .build();

        getlocation.setRealtimeWeather(realtimeWeather);

        // when
        Location updatedLocation = repository.save(getlocation);

        // then
        assertEquals(updatedLocation.getRealtimeWeather().getLocationCode(), code);
        assertEquals(updatedLocation.getRealtimeWeather().getTemperature(), -1);
        assertEquals(updatedLocation.getRealtimeWeather().getPrecipitation(), 40);
        assertEquals(updatedLocation.getRealtimeWeather().getStatus(), "Snowy");
        assertEquals(updatedLocation.getRealtimeWeather().getWindSpeed(), 15);
        assertEquals(updatedLocation.getRealtimeWeather().getLastUpdated(), LocalDateTime.of(2023, 3, 2, 15, 30));
    }


    @Test
    public void testAddHourlyWeatherData() {
        Location location = repository.findById("NYC_USA").get();

        List<HourlyWeather> listHourlyWeather = location.getListHourlyWeather();

        HourlyWeather forecast1 = new HourlyWeather().id(location, 10)
                .temperature(15)
                .precipitation(40)
                .status("Sunny");
        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        listHourlyWeather.add(forecast1);
        listHourlyWeather.add(forecast2);

        Location updatedLocation = repository.save(location);

        assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
    }

    @Test
    public void testFindByCountryCodeAndCityNameNotFound() {
        String countryCode = "IN";
        String cityName = "Delhi";

        Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityNameFound() {

        String countryCode = "US";
        String cityName = "New York City";

        Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNotNull();
        assertThat(location.getCountryCode()).isEqualTo(countryCode);
        assertThat(location.getCityName()).isEqualTo(cityName);
    }
}

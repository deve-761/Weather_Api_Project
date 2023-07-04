package com.skyapi.weatherforecast.location.service;

import com.skyapi.weatherforecast.base.BaseServiceTest;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LocationApiServiceTests extends BaseServiceTest {

    @InjectMocks
    private LocationService service;

    @Mock
    private LocationRepository locationRepository;


    @Test
    public void shouldReturnLocationWhenNewLocation(){

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
        when(locationRepository.save(location)).thenReturn(location);

        // then
        Location savedLocation = service.add(location);
        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
        assertThat(savedLocation.getCityName()).isEqualTo("New York City");
        assertThat(savedLocation.getRegionName()).isEqualTo("New York");
        assertThat(savedLocation.getCountryCode()).isEqualTo("US");
        assertThat(savedLocation.getCountryName()).isEqualTo("United States of America");
        assertThat(savedLocation.isEnabled()).isEqualTo(true);
    }

    @Test
    public void shouldReturnLocationList(){

        // given
        Location location1 = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        Location location2 = Location.builder()
                .code("LACA_USA")
                .cityName("Los Angeles")
                .regionName("California")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        // when
        when(locationRepository.findUntrashed()).thenReturn(List.of(location1, location2));

        // then
        List<Location> locations = service.list();
        assertThat(locations).isNotNull();
        assertThat(locations.get(0).getCode()).isEqualTo("NYC_USA");
        assertThat(locations.get(0).getCityName()).isEqualTo("New York City");
        assertThat(locations.get(0).getRegionName()).isEqualTo("New York");
        assertThat(locations.get(0).getCountryCode()).isEqualTo("US");
        assertThat(locations.get(0).getCountryName()).isEqualTo("United States of America");
        assertThat(locations.get(0).isEnabled()).isEqualTo(true);
        assertThat(locations.get(1).getCode()).isEqualTo("LACA_USA");
        assertThat(locations.get(1).getCityName()).isEqualTo("Los Angeles");
        assertThat(locations.get(1).getRegionName()).isEqualTo("California");
        assertThat(locations.get(1).getCountryCode()).isEqualTo("US");
        assertThat(locations.get(1).getCountryName()).isEqualTo("United States of America");
        assertThat(locations.get(1).isEnabled()).isEqualTo(true);
    }


    @Test
    public void shouldReturnLocation(){

        // given
        Location location1 = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String code = "NYC_USA";

        // when
        when(locationRepository.findByCode(code)).thenReturn(location1);

        // then
        Location locationReturn = service.get(code);
        assertThat(locationReturn).isNotNull();
        assertThat(locationReturn.getCode()).isEqualTo("NYC_USA");
        assertThat(locationReturn.getCityName()).isEqualTo("New York City");
        assertThat(locationReturn.getRegionName()).isEqualTo("New York");
        assertThat(locationReturn.getCountryCode()).isEqualTo("US");
        assertThat(locationReturn.getCountryName()).isEqualTo("United States of America");
        assertThat(locationReturn.isEnabled()).isEqualTo(true);

    }

    @Test
    public void givenLocationObject_whenUpdateLocation_thenReturnLocation(){

        // given
        Location savedLocation = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .trashed(false)
                .build();

        String code = "NYC_USA";

        Location changedLocation = savedLocation.toBuilder()
                .code("NYC_USA")
                .cityName("New York City Updated")
                .regionName("New York Updated")
                .countryCode("US")
                .countryName("United States of America Updated")
                .enabled(true)
                .trashed(false)
                .build();

        // when
        when(locationRepository.findByCode(code)).thenReturn(savedLocation);
        when(locationRepository.save(changedLocation)).thenReturn(changedLocation);

        // then
        Location location = service.get(code);

        location.setCityName(changedLocation.getCityName());
        location.setRegionName(changedLocation.getRegionName());
        location.setCountryCode(changedLocation.getCountryCode());
        location.setCountryName(changedLocation.getCountryName());
        location.setEnabled(changedLocation.isEnabled());
        location.setTrashed(changedLocation.isEnabled());

        Location updatedLocation =  locationRepository.save(location);

        assertThat(updatedLocation).isNotNull();
        assertThat(updatedLocation.getCode()).isEqualTo("NYC_USA");
        assertThat(updatedLocation.getCityName()).isEqualTo("New York City Updated");
        assertThat(updatedLocation.getRegionName()).isEqualTo("New York Updated");
        assertThat(updatedLocation.getCountryCode()).isEqualTo("US");
        assertThat(updatedLocation.getCountryName()).isEqualTo("United States of America Updated");
        assertThat(updatedLocation.isEnabled()).isEqualTo(true);
        assertThat(updatedLocation.isTrashed()).isEqualTo(false);
    }

    @Test
    public void givenLocationObject_whenDeleteLocation_thenReturnNullObject() throws LocationNotFoundException {

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
        when(locationRepository.findByCode(code)).thenReturn(location);
        doNothing().when(locationRepository).trashByCode(code);

        service.delete(code);

        // Assert
        verify(locationRepository, times(1)).trashByCode(code);
    }
}

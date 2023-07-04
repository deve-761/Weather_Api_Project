package com.skyapi.weatherforecast.realtime.service;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.repository.LocationRepository;
import com.skyapi.weatherforecast.realtime.repository.RealtimeWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RealtimeWeatherService {

    private final RealtimeWeatherRepository realtimeWeatherRepo;
    private final LocationRepository locationRepo;

    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given country code and city name");
        }

        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByLocationCode(locationCode);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given code: " + locationCode);
        }

        return realtimeWeather;
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) throws LocationNotFoundException {
        Location location = locationRepo.findByCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException("No location found with the given code: " + locationCode);
        }

        realtimeWeather.setLocation(location);
        realtimeWeather.setLastUpdated(LocalDateTime.now());

        if (location.getRealtimeWeather() == null) {
            location.setRealtimeWeather(realtimeWeather);
            Location updatedLocation = locationRepo.save(location);

            return updatedLocation.getRealtimeWeather();
        }

        return realtimeWeatherRepo.save(realtimeWeather);
    }
}

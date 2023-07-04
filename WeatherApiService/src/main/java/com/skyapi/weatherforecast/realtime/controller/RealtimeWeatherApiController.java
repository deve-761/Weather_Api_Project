package com.skyapi.weatherforecast.realtime.controller;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.realtime.dto.RealtimeWeatherDTO;
import com.skyapi.weatherforecast.location.service.GeolocationService;
import com.skyapi.weatherforecast.realtime.service.RealtimeWeatherService;
import com.skyapi.weatherforecast.location.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
@RequiredArgsConstructor
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);

    private final GeolocationService locationService;
    private final RealtimeWeatherService realtimeWeatherService;

    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = locationService.getLocation(ipAddress);
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);

        return ResponseEntity.ok(entity2DTO(realtimeWeather));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);

        return ResponseEntity.ok(entity2DTO(realtimeWeather));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeather(@PathVariable("locationCode") String locationCode,
                                                   @RequestBody @Valid RealtimeWeather realtimeWeatherInRequest) {

        realtimeWeatherInRequest.setLocationCode(locationCode);

        RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);

        return ResponseEntity.ok(entity2DTO(updatedRealtimeWeather));
    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }
}

package com.skyapi.weatherforecast.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.base.BaseRestControllerTest;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.dto.LocationDTO;
import com.skyapi.weatherforecast.location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.CoreMatchers.is;

public class LocationApiControllerTests extends BaseRestControllerTest {

    private static final String END_POINT_PATH = "/v1/locations";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    LocationService service;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {

        // given
        LocationDTO locationDTO = new LocationDTO();

        // when

        String bodyContent = mapper.writeValueAsString(locationDTO);

        // then
        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {

        // given
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();


        LocationDTO locationDTO = LocationDTO.builder()
                .code(location.getCode())
                .cityName(location.getCityName())
                .regionName(location.getRegionName())
                .countryCode(location.getCountryCode())
                .countryName(location.getCountryName())
                .enabled(location.isEnabled())
                .build();

        // when
        when(service.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(locationDTO);

        // then
        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYC_USA"))
                .andDo(print());
    }

    @Test
    public void testListShouldReturn204NoContent() throws Exception {

        // given

        // when
        when(service.list()).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testListShouldReturnLocationList() throws Exception {

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
        when(service.list()).thenReturn(List.of(location1, location2));

        // then
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].code", is("NYC_USA")))
                .andExpect(jsonPath("$[0].city_name", is("New York City")))
                .andExpect(jsonPath("$[0].region_name", is("New York")))
                .andExpect(jsonPath("$[0].country_name", is("United States of America")))
                .andExpect(jsonPath("$[0].country_code", is("US")))
                .andExpect(jsonPath("$[0].enabled", is(true)))
                .andExpect(jsonPath("$[1].code", is("LACA_USA")))
                .andExpect(jsonPath("$[1].city_name", is("Los Angeles")))
                .andExpect(jsonPath("$[1].region_name", is("California")))
                .andExpect(jsonPath("$[1].country_name", is("United States of America")))
                .andExpect(jsonPath("$[1].country_code", is("US")))
                .andExpect(jsonPath("$[1].enabled", is(true)))
                .andDo(print());
    }

    @Test
    public void testGetLocationShouldReturn405MethodNotAllowed() throws Exception {

        // given
        // when

        // then
        String requestURI = END_POINT_PATH + "/ABCDEF";
        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    public void testGetLocationShouldReturn404NotFound() throws Exception {

        // given
        // when

        // then
        String requestURI = END_POINT_PATH + "/ABCDEF";
        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetLocationShouldReturn200OK() throws Exception {

        // given
        String code = "LACA_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Location location = Location.builder()
                .code("LACA_USA")
                .cityName("Los Angeles")
                .regionName("California")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        // when
        when(service.get(code)).thenReturn(location);

        // then
        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("LACA_USA")))
                .andExpect(jsonPath("$.city_name", is("Los Angeles")))
                .andExpect(jsonPath("$.region_name", is("California")))
                .andExpect(jsonPath("$.country_name", is("United States of America")))
                .andExpect(jsonPath("$.country_code", is("US")))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {

        LocationDTO locationDTO = LocationDTO.builder()
                .code("ABCDEF")
                .cityName("Los Angeles")
                .regionName("California")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        LocationNotFoundException ex = new LocationNotFoundException(locationDTO.getCityName());

        when(service.update(any())).thenThrow(ex);

        String bodyContent = mapper.writeValueAsString(locationDTO);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDetails[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {

        Location location = Location.builder()
                .cityName("Los Angeles")
                .regionName("California")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .cityName(location.getCityName())
                .regionName(location.getRegionName())
                .countryCode(location.getCountryCode())
                .countryName(location.getCountryName())
                .enabled(location.isEnabled())
                .build();


        String bodyContent = mapper.writeValueAsString(locationDTO);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {

        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .code(location.getCode())
                .cityName(location.getCityName())
                .regionName(location.getRegionName())
                .countryCode(location.getCountryCode())
                .countryName(location.getCountryName())
                .enabled(location.isEnabled())
                .build();

        when(service.update(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(locationDTO);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(jsonPath("$.region_name", is("New York")))
                .andExpect(jsonPath("$.country_code", is("US")))
                .andExpect(jsonPath("$.country_name", is("United States of America")))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andDo(print());
    }

    @Test
    public void testDeleteLocationShouldReturn404NotFound() throws Exception {

        String code = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        LocationNotFoundException ex = new LocationNotFoundException(code);

        doThrow(ex).when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDetails[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testDeleteLocationShouldReturn204NoContent() throws Exception {

        String code = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyLocationCodeNotNull() throws Exception {

        LocationDTO locationDTO = LocationDTO.builder()
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String bodyContent = mapper.writeValueAsString(locationDTO);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errorDetails[0]", is("Location code cannot be null")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyLocationCodeLength() throws Exception {

        LocationDTO locationDTO = LocationDTO.builder()
                .code("")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .enabled(true)
                .build();

        String bodyContent = mapper.writeValueAsString(locationDTO);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errorDetails[0]", is("Location code must have 3-12 characters")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
        LocationDTO locationDTO = LocationDTO.builder()
                .regionName("")
                .build();

        String bodyContent = mapper.writeValueAsString(locationDTO);

        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH)
                .contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Location code cannot be null");
        assertThat(responseBody).contains("City name cannot be null");
        assertThat(responseBody).contains("Region name must have 3-128 characters");
        assertThat(responseBody).contains("Country name cannot be null");
        assertThat(responseBody).contains("Country code cannot be null");
    }
}

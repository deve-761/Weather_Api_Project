package com.skyapi.weatherforecast.ip2location;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IP2LocationTests {

    private String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    IP2Location ipLocator = new IP2Location();

    @BeforeEach
    void setup() throws IOException{
        InputStream inputStream = getClass().getResourceAsStream(DBPath);
        byte[] data = inputStream.readAllBytes();
        ipLocator.Open(data);
        inputStream.close();
    }

    @Test
    public void testInvalidIP() throws IOException {

        String ipAddress = "abc";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertEquals(ipResult.getStatus(),"INVALID_IP_ADDRESS");

        System.out.println(ipResult);

    }

    @Test
    public void testValidIP1() throws IOException {

        String ipAddress = "108.30.178.78"; // New York
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertEquals(ipResult.getStatus(),"OK");
        assertEquals(ipResult.getCity(),"New York City");

        System.out.println(ipResult);
    }

    @Test
    public void testValidIP2() throws IOException {

        String ipAddress = "103.48.198.141"; // Delhi
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertEquals(ipResult.getStatus(),"OK");
        assertEquals(ipResult.getCity(),"Delhi");

        System.out.println(ipResult);
    }
}

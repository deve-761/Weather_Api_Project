package com.skyapi.weatherforecast.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDTO {

    private LocalDateTime timestamp;
    private int status;
    private String path;
    private HttpStatus httpStatus;
    List<String> errorDetails;

}

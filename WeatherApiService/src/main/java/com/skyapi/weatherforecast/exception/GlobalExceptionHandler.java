package com.skyapi.weatherforecast.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // handleMissingServletRequestParameter : triggers when there are missing parameters
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> details = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage() + " is not supported");
        details.add(builder.toString());

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());

        ErrorDTO error = new ErrorDTO.ErrorDTOBuilder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorDetails(details)
                .path(request.getContextPath())
                .httpStatus(httpStatus)
                .build();

        LOGGER.error("GlobalExceptionHandler | handleHttpRequestMethodNotSupported | ex : " + ex );

        return ResponseEntity.status(status).body(error);

    }

    // handleMethodArgumentNotValid : triggers when @Valid fails
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> details = new ArrayList<String>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
                String errorMessage = err.getDefaultMessage();
                details.add(errorMessage);
                }
        );

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());

        ErrorDTO error = new ErrorDTO.ErrorDTOBuilder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorDetails(details)
                .path(request.getContextPath())
                .httpStatus(httpStatus)
                .build();

        //Map<String, String> errors = new HashMap<>();
        //ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        LOGGER.error("GlobalExceptionHandler | handleMethodArgumentNotValid | ex : " + ex );

        return new ResponseEntity<>(error, headers, status);
    }

    // handleMissingServletRequestParameter : triggers when there are missing parameters
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


        List<String> details = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getParameterName());
        details.add(builder.toString());

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());

        ErrorDTO error = new ErrorDTO.ErrorDTOBuilder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorDetails(details)
                .path(request.getContextPath())
                .httpStatus(httpStatus)
                .build();

        LOGGER.error("GlobalExceptionHandler | handleMissingServletRequestParameter | ex : " + ex );

        return ResponseEntity.status(status).body(error);

    }

    // handleHttpMessageNotReadable : triggers when the JSON is malformed
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> details = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage());
        details.add(builder.toString());

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());

        ErrorDTO error = new ErrorDTO.ErrorDTOBuilder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorDetails(details)
                .path(request.getContextPath())
                .httpStatus(httpStatus)
                .build();

        LOGGER.error("GlobalExceptionHandler | handleHttpMessageNotReadable | ex : " + ex );

        return ResponseEntity.status(status).body(error);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(HttpServletRequest request, Exception ex) {

        /*
            List<String> stackTraceList = new ArrayList<>();
            for (String line : Arrays.toString(ex.getStackTrace()).split("\n")) {
                stackTraceList.add(line.trim());
            }


        List<String> details = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .map(String::trim)
                .collect(Collectors.toList());

         */

        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        ErrorDTO error = new ErrorDTO.ErrorDTOBuilder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorDetails(details)
                .path(request.getServletPath())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        LOGGER.error("GlobalExceptionHandler | handleHttpMessageNotReadable | ex : " + ex );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    @ExceptionHandler({BadRequestException.class, GeolocationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex) {

        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        ErrorDTO error = ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorDetails(details)
                .path(request.getServletPath())
                .build();

        LOGGER.error("GlobalExceptionHandler | handleBadRequestException | ex : " + ex );

        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex) {

        List<String> details = new ArrayList<String>();

        ConstraintViolationException violationException = (ConstraintViolationException) ex;

        var constraintViolations = violationException.getConstraintViolations();

        constraintViolations.forEach(constraint -> {
            details.add(constraint.getPropertyPath() + ": " + constraint.getMessage());
        });

        ErrorDTO error = ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorDetails(details)
                .path(request.getServletPath())
                .build();

        LOGGER.error("GlobalExceptionHandler | handleConstraintViolationException | ex : " + ex );

        return error;
    }


    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception ex) {

        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        ErrorDTO error = ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .errorDetails(details)
                .path(request.getServletPath())
                .build();


        LOGGER.error("GlobalExceptionHandler | handleLocationNotFoundException | ex : " + ex );

        return error;
    }
}

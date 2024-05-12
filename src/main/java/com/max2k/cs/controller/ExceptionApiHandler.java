package com.max2k.cs.controller;

import com.max2k.cs.DTO.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleException(ResponseStatusException exception){
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ApiErrorDTO.builder()
                        .message(exception.getReason())
                        .timestamp(Instant.now())
                        .code(exception.getStatusCode().toString())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorDTO.builder()
                        .message(String.join(", ", details))
                        .timestamp(Instant.now())
                        .code(HttpStatus.BAD_REQUEST.value()+"")
                        .build()
                );
    }

}

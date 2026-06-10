package com.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    private int status;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(status)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Object> error(String message, Object errors, int status) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .errors(errors)
                .status(status)
                .timestamp(Instant.now())
                .build();
    }
}

package com.ohgiraffers.team3backendscm.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Common REST API response wrapper.
 *
 * @param <T> response payload type
 */
@Getter
@Builder
public class ApiResponse<T> {

    private Boolean success;
    private T data;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> failure(String errorCode, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .errorCode(errorCode)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
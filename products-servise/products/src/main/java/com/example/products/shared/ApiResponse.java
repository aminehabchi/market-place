package com.example.products.shared;

import java.util.Map;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        int statusCode,
        Map<String, String> errors) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, 200, null);
    }

    public static <T> ApiResponse<T> successStatus(int status) {
        return new ApiResponse<>(true, null, null, status, null);
    }

    public static <T> ApiResponse<T> successStatus(HttpStatus status) {
        return new ApiResponse<>(true, null, null, status.value(), null);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(false, null, message, status, null);
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(false, null, message, status.value(), null);
    }
}
package com.buy01.users.Utils;

import java.util.Map;

import org.springframework.http.HttpStatus;

public record ApiResponseUtils<T>(
        boolean success,
        T data,
        String message,
        int statusCode,
        Map<String, String> errors) {
    public static <T> ApiResponseUtils<T> success(T data) {
        return new ApiResponseUtils<>(true, data, null, 200, null);
    }

    public static <T> ApiResponseUtils<T> successStatus(int status) {
        return new ApiResponseUtils<>(true, null, null, status, null);
    }

    public static <T> ApiResponseUtils<T> successStatus(HttpStatus status) {
        return new ApiResponseUtils<>(true, null, null, status.value(), null);
    }

    public static <T> ApiResponseUtils<T> error(String message, int status) {
        return new ApiResponseUtils<>(false, null, message, status, null);
    }

    public static <T> ApiResponseUtils<T> error(String message, HttpStatus status) {
        return new ApiResponseUtils<>(false, null, message, status.value(), null);
    }
}
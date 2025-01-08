package com.mycom.socket.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
        @Builder
        public  ApiResponse {
        }
        public static <T> ApiResponse<T> success(String message) {
                return ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build();
        }

        public static <T> ApiResponse<T> success(String message, T data) {
                return ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build();
        }

        public static <T> ApiResponse<T> error(String message) {
                return ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build();
        }

        public static <T> ApiResponse<T> error(String message, T data) {
                return ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build();
        }

}
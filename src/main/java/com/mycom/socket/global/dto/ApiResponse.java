package com.mycom.socket.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

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

        private static LocalDateTime getCurrentTimestamp() {
                return LocalDateTime.now();
        }
        public static <T> ApiResponse<T> success(String message) {
                Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
                return ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .timestamp(getCurrentTimestamp())
                        .build();
        }

        public static <T> ApiResponse<T> success(String message, T data) {
                Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
                return ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .timestamp(getCurrentTimestamp())
                        .build();
        }

        public static <T> ApiResponse<T> error(String message) {
                Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
                return ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .timestamp(getCurrentTimestamp())
                        .build();
        }

        public static <T> ApiResponse<T> error(String message, T data) {
                Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
                return ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .data(data)
                        .timestamp(getCurrentTimestamp())
                        .build();
        }
}
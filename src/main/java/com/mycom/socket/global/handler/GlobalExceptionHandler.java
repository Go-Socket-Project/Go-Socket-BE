package com.mycom.socket.global.handler;

import com.mycom.socket.global.dto.ApiResponse;
import com.mycom.socket.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {



    // 비지니스 예외 처리
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ApiResponse<?>> handleBaseException(BaseException e) {
        log.error("BaseException : {}", e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    // Valid 또는 Validated 바인딩 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Validation Exception: {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorMessage));
    }

   // 일반적인 예외 처리 (IllegalArgumentException, NoSuchElementException 등)
    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class, NoResourceFoundException.class})
    protected ResponseEntity<ApiResponse<?>> handleCommonException(Exception e) {
        HttpStatus status = (e instanceof NoResourceFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        log.error("Common Exception : {}", e.getMessage());
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(e.getMessage()));
    }
    // 모든 예외 처리 (최후의 보루)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleAllException(Exception e) {
        log.error("Internal Server Error", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage() != null ? e.getMessage() : "서버 내부 오류가 발생했습니다."));
    }
}

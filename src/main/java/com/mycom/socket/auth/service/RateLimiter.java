package com.mycom.socket.auth.service;

import com.mycom.socket.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimiter {
    private final Map<String, List<LocalDateTime>> requestMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 3;  // 1분당 최대 3번
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(1);  // 1분의 시간 간격

    public void checkRateLimit(String email) {
        List<LocalDateTime> requests = requestMap.computeIfAbsent(email, k -> new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();

        requests.removeIf(requestTime ->
                requestTime.plus(WINDOW_SIZE).isBefore(now));

        if (requests.size() >= MAX_REQUESTS) {
            throw new BaseException("너무 많은 요청입니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS);
        }

        requests.add(now);
    }
}

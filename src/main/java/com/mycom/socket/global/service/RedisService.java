package com.mycom.socket.global.service;

import com.mycom.socket.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long VERIFICATION_TTL = 180; // 3분

    // 인증 코드 저장
    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, VERIFICATION_TTL, TimeUnit.SECONDS);
    }

    // 인증 코드 조회
    public String getCode(String email) {
        Object code = redisTemplate.opsForValue().get(email);
        if (code == null) {
            throw new BaseException("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        return code.toString();
    }

    // 요청 횟수 증가 (rate limiting)
    public Long incrementCount(String email) {
        Long count = redisTemplate.opsForValue().increment(email);
        if (count == 1) {
            redisTemplate.expire(email, 60, TimeUnit.SECONDS);
        }
        return count;
    }
}

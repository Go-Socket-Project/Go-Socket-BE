package com.mycom.socket.global.service;

import com.mycom.socket.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key Prefix 상수
     */
    private static final String VERIFIED_EMAIL_PREFIX = "verified:email:";
    private static final String RATE_LIMIT_PREFIX = "rate-limit:";

    /**
     * Redis TTL 상수
     */
    private static final Duration VERIFICATION_TTL = Duration.ofMinutes(3);  // 인증번호 유효시간
    private static final Duration VERIFIED_EMAIL_TTL = Duration.ofMinutes(30); // 인증된 이메일 유효시간
    private static final Duration RATE_LIMIT_TTL = Duration.ofMinutes(1); // 요청 제한 시간


    /**
     * 인증번호를 Redis에 저장
     * Key와 Value로 동일한 인증번호를 사용
     * 3분 후 자동 삭제
     */
    public void saveCode(String code) {
        redisTemplate.opsForValue().set(code, code, VERIFICATION_TTL);
    }

    /**
     * Redis에서 인증번호 조회
     * 인증번호가 존재하지 않거나 만료된 경우 예외 발생
     * @throws BaseException 인증번호가 만료되었거나 존재하지 않는 경우
     */
    public String getCode(String code) {
        Object savedCode = redisTemplate.opsForValue().get(code);
        if (savedCode == null) {
            throw new BaseException("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        return savedCode.toString();
    }

    /**
     * 이메일별 요청 횟수 증가 (Rate Limiting)
     * 첫 요청시 1분 후 자동 삭제되도록 설정
     * @return 현재 요청 횟수
     */
    public Long incrementCount(String email) {
        String key = RATE_LIMIT_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, RATE_LIMIT_TTL);
        }
        return count;
    }

    /**
     * 인증된 이메일 정보 저장
     * 30분 동안 유효
     */
    public void saveVerifiedEmail(String email) {
        redisTemplate.opsForValue().set(
                VERIFIED_EMAIL_PREFIX + email,
                "true",
                VERIFIED_EMAIL_TTL
        );
    }

    /**
     * 이메일 인증 여부 확인
     * @return 이메일이 인증되었으면 true, 아니면 false
     */
    public boolean isEmailVerified(String email) {
        Object verified = redisTemplate.opsForValue().get(VERIFIED_EMAIL_PREFIX + email);
        return "true".equals(verified);
    }

}

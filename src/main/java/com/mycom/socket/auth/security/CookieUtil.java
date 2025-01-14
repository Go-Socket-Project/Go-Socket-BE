package com.mycom.socket.auth.security;

import com.mycom.socket.auth.config.JWTProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {
    private final JWTProperties jwtProperties;

    /**
     * 공통 쿠키 생성 메소드
     * 모든 종류의 쿠키 생성에 사용되는 기본 메소드입니다.
     *
     * @param name 쿠키의 이름
     * @param value 쿠키에 저장될 값 (토큰)
     * @param maxAge 쿠키의 유효 시간 (초 단위)
     * @param secure HTTPS 프로토콜에서만 전송 여부
     * @return 생성된 쿠키 객체
     */
    private Cookie createCookie(String name, String value, long maxAge, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAge);
        cookie.setAttribute("SameSite", "Strict"); //CSRF 공격 방지 설정 추가
        return cookie;
    }

    /**
     * Access Token을 저장하는 쿠키 생성
     * 클라이언트 인증에 사용되는 Access Token을 쿠키에 저장합니다.
     *
     * @param token JWT Access Token 문자열
     * @return Access Token이 저장된 쿠키
     */
    public Cookie createAuthCookie(String token) {
        return createCookie(
                jwtProperties.getAccessTokenCookieName(),
                token,
                jwtProperties.getAccessTokenValidityInSeconds(),
                jwtProperties.isSecureCookie()
        );
    }

    /**
     * Refresh Token을 저장하는 쿠키 생성
     * Access Token 재발급에 사용되는 Refresh Token을 쿠키에 저장합니다.
     *
     * @param token JWT Refresh Token 문자열
     * @return Refresh Token이 저장된 쿠키
     */
    public Cookie createRefreshCookie(String token) {
        return createCookie(
                jwtProperties.getRefreshTokenCookieName(),
                token,
                jwtProperties.getRefreshTokenValidityInSeconds(),
                jwtProperties.isSecureCookie()
        );
    }

    /**
     * 만료된 쿠키 생성
     * 로그아웃 또는 토큰 무효화 시 기존 쿠키를 만료시키기 위해 사용됩니다.
     *
     * @param name 만료시킬 쿠키의 이름
     * @return 즉시 만료되도록 설정된 쿠키
     */
    public Cookie createExpiredCookie(String name) {
        return createCookie(name, null, 0, true);
    }
}

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
     * 인증 쿠키 생성
     */
    public Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie(jwtProperties.getAccessTokenCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isSecureCookie());
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtProperties.getAccessTokenValidityInSeconds());
        return cookie;
    }

    /**
     * 리프레시 토큰 쿠키 생성
     */
    public Cookie createRefreshCookie(String token) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isSecureCookie());
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtProperties.getRefreshTokenValidityInSeconds());
        return cookie;
    }


    /**
     * 인증 쿠키 만료 처리
     */
    public Cookie createExpiredAuthCookie() {
        Cookie cookie = new Cookie(jwtProperties.getAccessTokenCookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        return cookie;
    }

    /**
     * 리프레시 토큰 쿠키 만료 처리
     */
    public Cookie createExpiredRefreshCookie() {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        return cookie;
    }
}

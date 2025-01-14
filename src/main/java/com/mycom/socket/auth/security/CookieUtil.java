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
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isSecureCookie());
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtProperties.getAccessTokenValidityInSeconds());
        return cookie;
    }

    /**
     * 인증 쿠키 만료 처리
     */
    public Cookie createExpiredAuthCookie() {
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        return cookie;
    }
}

package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.config.JWTProperties;
import com.mycom.socket.auth.dto.response.TokenResponse;
import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.auth.security.CookieUtil;
import com.mycom.socket.global.exception.BadRequestException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JWTProperties jwtProperties;


    @PostMapping("/refresh")
    public TokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = extractRefreshToken(request);
        if (refreshTokenOpt.isEmpty()) {
            return TokenResponse.of("리프레시 토큰이 없습니다. 다시 로그인해주세요.");
        }

        String refreshToken = refreshTokenOpt.get();
        if (!jwtUtil.validateToken(refreshToken, "REFRESH_TOKEN")) {
            response.addCookie(cookieUtil.createExpiredCookie(jwtProperties.getRefreshTokenCookieName()));
            return TokenResponse.of("유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        String newAccessToken = jwtUtil.createToken(email, jwtProperties.getAccessTokenValidityInSeconds(), "ACCESS_TOKEN");
        String newRefreshToken = jwtUtil.createToken(email, jwtProperties.getRefreshTokenValidityInSeconds(), "REFRESH_TOKEN");

        response.addCookie(cookieUtil.createAuthCookie(newAccessToken));
        response.addCookie(cookieUtil.createRefreshCookie(newRefreshToken));

        return TokenResponse.of(newAccessToken);
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> jwtProperties.getRefreshTokenCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
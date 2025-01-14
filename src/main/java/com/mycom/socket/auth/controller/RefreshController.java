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

@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JWTProperties jwtProperties;

    @PostMapping("/refresh")
    public TokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        try {
            jwtUtil.validateToken(refreshToken);
        } catch (JwtException e) {
            response.addCookie(cookieUtil.createExpiredRefreshCookie());
            throw new BadRequestException("유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        String newAccessToken = jwtUtil.createToken(email, jwtProperties.getAccessTokenValidityInSeconds());

        Cookie accessTokenCookie = cookieUtil.createAuthCookie(newAccessToken);
        response.addCookie(accessTokenCookie);

        String newRefreshToken = jwtUtil.createToken(email, jwtProperties.getRefreshTokenValidityInSeconds());
        Cookie refreshTokenCookie = cookieUtil.createRefreshCookie(newRefreshToken);
        response.addCookie(refreshTokenCookie);

        return TokenResponse.of(newAccessToken);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new BadRequestException("리프레시 토큰이 없습니다. 다시 로그인해주세요.");
        }

        for (Cookie cookie : request.getCookies()) {
            if (jwtProperties.getRefreshTokenCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new BadRequestException("리프레시 토큰이 없습니다. 다시 로그인해주세요.");
    }
}
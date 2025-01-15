package com.mycom.socket.auth.jwt;

import com.mycom.socket.auth.config.JWTProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private final SecretKey secretKey;
    private final JWTProperties jwtProperties;

    /**
     * JWTUtil 생성자
     * 설정된 시크릿 키를 바탕으로 HMAC-SHA 알고리즘용 SecretKey를 생성합니다.
     *
     * @param jwtProperties JWT 관련 설정값을 담고 있는 프로퍼티 객체
     */
    public JWTUtil(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * JWT Parser 생성
     * 토큰 검증 및 정보 추출에 사용되는 공통 Parser를 생성합니다.
     *
     * @return 설정된 JWT Parser 객체
     */
    private JwtParser createParser() {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build();
    }


    /**
     * JWT 토큰 생성
     * 주어진 이메일과 유효기간으로 새로운 JWT를 생성합니다.
     *
     * @param email             토큰에 포함될 사용자 이메일
     * @param validityInSeconds 토큰 유효 기간 (초)
     * @param accessToken
     * @return 생성된 JWT 문자열
     * @throws IllegalStateException 토큰 생성 중 오류 발생 시
     */
    public String createToken(String email, long validityInSeconds, String accessToken) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + (validityInSeconds * 1000));

        try {
            return Jwts.builder()
                    .issuer(jwtProperties.getIssuer())
                    .subject(email)
                    .issuedAt(now)
                    .expiration(validity)
                    .claim("type", accessToken)
                    .signWith(secretKey)
                    .compact();
        } catch (JwtException e) {
            log.error("토큰 생성 중 오류가 발생했습니다.", e);
            throw new IllegalStateException("토큰 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 토큰 유효성 검증
     * 주어진 토큰이 유효한지 검사합니다. 토큰의 서명, 만료 여부, 발급자 등을 확인합니다.
     *
     * @param token 검증할 JWT 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token, String expectedType) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            var claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 토큰 타입 검증
            String tokenType = claims.get("type", String.class);
            if (!expectedType.equals(tokenType)) {
                log.warn("잘못된 토큰 타입입니다. expected: {}, actual: {}", expectedType, tokenType);
                return false;
            }

            return new Date().before(claims.getExpiration());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            return false;
        } catch (JwtException e) {
            log.warn("유효하지 않은 JWT 토큰입니다.", e);
            return false;
        }
    }

    /**
     * 토큰의 만료 시간 추출
     *
     * @param token JWT 문자열
     * @return 토큰의 만료 시간
     */
    private Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }


    /**
     * 토큰에서 사용자 이메일 추출
     * JWT의 subject 클레임에서 사용자 이메일을 추출합니다.
     *
     * @param token JWT 문자열
     * @return 토큰에 포함된 사용자 이메일
     * @throws IllegalStateException 토큰에서 이메일을 추출할 수 없는 경우
     */
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
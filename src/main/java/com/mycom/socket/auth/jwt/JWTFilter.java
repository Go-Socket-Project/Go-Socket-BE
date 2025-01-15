package com.mycom.socket.auth.jwt;

import com.mycom.socket.auth.service.MemberDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;
    private final MemberDetailsService memberDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE = "ACCESS_TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Bearer 토큰 확인
            String bearerToken = resolveTokenFromHeader(request);
            if (isValidBearerToken(bearerToken)) {
                setAuthentication(bearerToken);
            }
        } catch (Exception e) {
            log.warn("인증 처리 실패", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setAuthentication(String token) {
        String email = jwtUtil.getEmail(token);
        UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isValidBearerToken(String token) {
        return StringUtils.hasText(token) &&
                token.matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$") &&
                jwtUtil.validateToken(token, TOKEN_TYPE);
    }
}
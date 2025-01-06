package com.mycom.socket.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.global.dto.ApiResponse;
import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
import com.mycom.socket.go_socket.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;  // JwtProvider 대신 JWTUtil 사용
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 처리 중 오류가 발생했습니다.", e);
        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        MemberDetails memberDetails = (MemberDetails) authResult.getPrincipal();
        Member member = memberDetails.getMember();

        String token = jwtUtil.createToken(member.getEmail());

        // HTTP Only 쿠키에 JWT 토큰 저장
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(1800);        // 쿠키 만료시간 30분

        // SameSite 속성 설정 추가
        response.setHeader("Set-Cookie",
                String.format("Authorization=%s; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=1800", token));

        LoginResponseDto loginResponse = new LoginResponseDto(
                member.getEmail(),
                member.getNickname()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.success("로그인 성공", loginResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorMessage = "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.";
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(errorMessage));
    }
}
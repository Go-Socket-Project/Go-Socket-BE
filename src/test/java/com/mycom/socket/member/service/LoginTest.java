package com.mycom.socket.member.service;

import com.mycom.socket.auth.dto.request.LoginRequest;
import com.mycom.socket.auth.dto.response.LoginResponse;
import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.global.exception.BadRequestException;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.entity.enums.MemberRole;
import com.mycom.socket.go_socket.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @Test
    void 로그인성공() {
        // given
        String email = "test@test.com";
        String password = "password";
        String nickname = "tester";
        String encodedPassword = "encodedPassword";
        String token = "test.token.here";

        LoginRequest request = new LoginRequest(email, password);
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(MemberRole.USER)
                .build();

        // when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.createToken(email)).thenReturn(token);

        LoginResponse response = authService.login(request, this.response);

        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(this.response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertAll(
                () -> assertEquals(email, response.email()),
                () -> assertEquals(nickname, response.nickname()),
                () -> assertEquals("Authorization", cookie.getName()),
                () -> assertEquals(token, cookie.getValue()),
                () -> assertTrue(cookie.isHttpOnly()),
                () -> assertTrue(cookie.getSecure()),
                () -> assertEquals("/", cookie.getPath()),
                () -> assertEquals(1800, cookie.getMaxAge())
        );

        verify(memberRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtUtil).createToken(email);
    }

    @Test
    void 로그인실패_이메일없음() {
        // given
        String email = "nonexistent@test.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(BadRequestException.class, () -> authService.login(request, response));
        verify(memberRepository).findByEmail(email);
        verifyNoInteractions(response);  // 쿠키가 설정되지 않았는지 확인
    }

    @Test
    void 로그인실패_비밀번호틀림() {
        // given
        String email = "test@test.com";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword";
        LoginRequest request = new LoginRequest(email, password);

        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .role(MemberRole.USER)
                .build();

        // when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // then
        assertThrows(BadRequestException.class, () -> authService.login(request, response));
        verify(memberRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verifyNoInteractions(response);  // 쿠키가 설정되지 않았는지 확인
    }
}
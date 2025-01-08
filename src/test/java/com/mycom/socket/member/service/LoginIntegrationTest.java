package com.mycom.socket.member.service;

import com.mycom.socket.auth.dto.request.LoginRequest;
import com.mycom.socket.auth.dto.response.LoginResponse;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.entity.enums.MemberRole;
import com.mycom.socket.go_socket.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoginIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        Member testMember = Member.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .nickname("tester")
                .role(MemberRole.USER)
                .build();

        memberRepository.save(testMember);
    }

    @Test
    void 로그인통합테스트() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password");
        HttpServletResponse response = new MockHttpServletResponse();

        // when
        LoginResponse loginResponse = authService.login(request, response);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie("Authorization");

        // then
        assertAll(
                () -> assertEquals("test@test.com", loginResponse.email()),
                () -> assertEquals("tester", loginResponse.nickname()),
                () -> assertNotNull(cookie),
                () -> assertTrue(cookie.isHttpOnly()),
                () -> assertTrue(cookie.getSecure()),
                () -> assertEquals("/", cookie.getPath()),
                () -> assertEquals(1800, cookie.getMaxAge())
        );
    }
}
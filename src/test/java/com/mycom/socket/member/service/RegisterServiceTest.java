package com.mycom.socket.member.service;

import com.mycom.socket.auth.dto.request.RegisterRequest;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.mycom.socket.global.exception.BadRequestException;
import com.mycom.socket.global.exception.ConflictException;
import com.mycom.socket.auth.service.MailService;
import com.mycom.socket.auth.dto.response.RegisterResponse;


@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;  // 추가 필요

    @Test
    void 회원가입_성공() {
        // given
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );

        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
        given(mailService.isEmailVerified(request.email())).willReturn(true);  // 이메일 인증 확인

        Member savedMember = Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password("encodedPassword")
                .intro(request.intro())
                .build();
        ReflectionTestUtils.setField(savedMember, "id", 1L);

        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        RegisterResponse response = authService.register(request);

        // then
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo(request.email());
        assertThat(response.nickname()).isEqualTo(request.nickname());
        assertThat(response.message()).isEqualTo("회원가입이 완료되었습니다.");

        verify(memberRepository).save(any(Member.class));
        verify(mailService).isEmailVerified(request.email());
    }

    @Test
    void 회원가입_실패_이메일_미인증() {
        // given
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );

        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
        given(mailService.isEmailVerified(request.email())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이메일 인증이 필요합니다. 이메일 인증을 먼저 완료해주세요.");
    }

    @Test
    void 회원가입_실패_이메일_중복() {
        // given
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );

        given(memberRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}

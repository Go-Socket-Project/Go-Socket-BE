package com.mycom.socket.member.service;

import com.mycom.socket.auth.dto.request.RegisterRequestDto;
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


@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입_성공() {
        // given
        RegisterRequestDto request = new RegisterRequestDto(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );

        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

        // save()를 호출했을 때 반환할 Member 객체 설정
        Member savedMember = Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password("encodedPassword")
                .intro(request.intro())
                .build();
        // ID 설정 (실제로는 DB가 생성)
        ReflectionTestUtils.setField(savedMember, "id", 1L);

        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        Long memberId = authService.register(request);

        // then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository).save(any(Member.class));
    }
}

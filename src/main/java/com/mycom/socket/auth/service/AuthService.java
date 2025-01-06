package com.mycom.socket.auth.service;

import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.global.exception.BadRequestException;
import com.mycom.socket.global.exception.ConflictException;
import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.entity.enums.MemberRole;
import com.mycom.socket.go_socket.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BadRequestException("잘못된 비밀번호입니다.");
        }

        String token = jwtUtil.createToken(member.getEmail());

        // 쿠키 생성 및 설정
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);    // JavaScript에서 접근 불가
        cookie.setSecure(true);      // HTTPS에서만 전송
        cookie.setPath("/");         // 모든 경로에서 접근 가능
        cookie.setMaxAge(1800);      // 30분
        response.addCookie(cookie);

        return LoginResponseDto.of(
                member.getEmail(),
                member.getNickname()
        );
    }

    // 이메일 인증 코드 전송

    // 이메일 인증 코드 만료

    @Transactional
    public Long register(RegisterRequestDto request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }

        // 이메일 인증 여부 확인

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .intro(request.intro())
                .role(MemberRole.USER)
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(cookie);
    }
}

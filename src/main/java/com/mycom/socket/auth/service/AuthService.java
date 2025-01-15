package com.mycom.socket.auth.service;

import com.mycom.socket.auth.config.JWTProperties;
import com.mycom.socket.auth.dto.response.RegisterResponse;
import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.auth.security.CookieUtil;
import com.mycom.socket.global.exception.BadRequestException;
import com.mycom.socket.global.exception.ConflictException;
import com.mycom.socket.auth.dto.request.LoginRequest;
import com.mycom.socket.auth.dto.request.RegisterRequest;
import com.mycom.socket.auth.dto.response.LoginResponse;
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

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final MailService mailService;
    private final CookieUtil cookieUtil;
    private final JWTProperties jwtProperties;

    /**
     * 사용자 로그인 처리
     * 이메일과 비밀번호를 검증하고 JWT 토큰을 생성하여 쿠키에 저장
     *
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @param response HTTP 응답 객체 (쿠키 저장용)
     * @return 로그인 성공 시 사용자 정보를 포함한 응답
     * @throws BadRequestException 잘못된 이메일이나 비밀번호인 경우
     */
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BadRequestException("잘못된 비밀번호입니다.");
        }

        String refreshToken = jwtUtil.createToken(member.getEmail(),
                jwtProperties.getRefreshTokenValidityInSeconds(), "ACCESS_TOKEN");

        Cookie refreshTokenCookie = cookieUtil.createRefreshCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        String accessToken = jwtUtil.createToken(member.getEmail(),
                jwtProperties.getAccessTokenValidityInSeconds(), "ACCESS_TOKEN");

        Cookie accessTokenCookie = cookieUtil.createAuthCookie(accessToken);
        response.addCookie(accessTokenCookie);

        return LoginResponse.of(member.getEmail(), member.getNickname());
    }

    /**
     * 회원 가입 처리
     * 이메일과 닉네임 중복 검사 후 새로운 회원 정보 저장
     *
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임, 자기소개)
     * @return 저장된 회원의 ID
     * @throws ConflictException 이메일 또는 닉네임이 이미 존재하는 경우
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }

        // 이메일 인증 여부 확인
        if (!mailService.isEmailVerified(request.email())) {
            throw new BadRequestException("이메일 인증이 필요합니다. 이메일 인증을 먼저 완료해주세요.");
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .intro(request.intro())
                .role(MemberRole.USER)
                .build();

        Member savedMember = memberRepository.save(member);
        return RegisterResponse.of(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }

    /**
     * 로그아웃 처리
     * Access Token과 Refresh Token 쿠키를 무효화하여 로그아웃을 수행합니다.
     *
     * @param response HTTP 응답 객체
     */
    public void logout(HttpServletResponse response) {
        response.addCookie(cookieUtil.createExpiredCookie(jwtProperties.getAccessTokenCookieName()));
        response.addCookie(cookieUtil.createExpiredCookie(jwtProperties.getRefreshTokenCookieName()));
    }
}

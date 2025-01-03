package com.mycom.socket.go_socket.service;

import com.mycom.socket.global.exception.ConflictException;
import com.mycom.socket.go_socket.dto.request.MemberRegisterDto;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.entity.enums.MemberRole;
import com.mycom.socket.go_socket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 인증 코드 전송

    // 이메일 인증 코드 만료

    @Transactional
    public Long register(MemberRegisterDto request) {
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
}

package com.mycom.socket.go_socket.service;

import com.mycom.socket.global.exception.NotFoundException;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member updateProfile(String email, String nickname, String intro) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        member.updateProfile(nickname, intro);
        return member;
    }

    @Transactional
    public void updatePassword(String email, String currentPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }

        member.updatePassword(passwordEncoder.encode(newPassword));
    }
}

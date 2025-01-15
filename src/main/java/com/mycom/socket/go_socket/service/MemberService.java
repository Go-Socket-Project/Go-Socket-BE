package com.mycom.socket.go_socket.service;


import com.mycom.socket.global.exception.NotFoundException;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }
}

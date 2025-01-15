package com.mycom.socket.go_socket.dto.response;

import com.mycom.socket.go_socket.entity.Member;

public record ProfileResponse(
        String email,
        String nickname,
        String intro
) {
    public static ProfileResponse of(Member member) {
        return new ProfileResponse(
                member.getEmail(),
                member.getNickname(),
                member.getIntro()
        );
    }
}

package com.mycom.socket.auth.dto.response;

public record RegisterResponse(
        Long memberId,
        String email,
        String nickname,
        String message
) {
    public static RegisterResponse of(Long memberId, String email, String nickname) {
        return new RegisterResponse(memberId, email, nickname, "회원가입이 완료되었습니다.");
    }
}

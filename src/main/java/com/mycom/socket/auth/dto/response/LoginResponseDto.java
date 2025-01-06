package com.mycom.socket.auth.dto.response;

public record LoginResponseDto(
        String email,
        String nickname
) {
    public static LoginResponseDto of(String email, String nickname) {
        return new LoginResponseDto(email, nickname);
    }
}

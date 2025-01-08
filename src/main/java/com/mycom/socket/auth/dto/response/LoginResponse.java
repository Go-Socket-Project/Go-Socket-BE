package com.mycom.socket.auth.dto.response;

public record LoginResponse(
        String email,
        String nickname
) {
    public static LoginResponse of(String email, String nickname) {
        return new LoginResponse(email, nickname);
    }
}

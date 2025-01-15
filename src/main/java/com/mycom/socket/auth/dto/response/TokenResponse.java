package com.mycom.socket.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String message,
        boolean success
) {
    public static TokenResponse of(String message) {
        return new TokenResponse(null, message, false);
    }
}

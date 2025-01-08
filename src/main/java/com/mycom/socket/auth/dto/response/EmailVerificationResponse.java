package com.mycom.socket.auth.dto.response;

public record EmailVerificationResponse(
        String message
) {
    public static EmailVerificationResponse of(String message) {
        return new EmailVerificationResponse(message);
    }
}


package com.mycom.socket.auth.service.data;

import java.time.LocalDateTime;

import java.time.Duration;

public record VerificationData(
        String code,
        LocalDateTime expiryTime,
        boolean verified
)  {

    private static final Duration CODE_VALID_DURATION = Duration.ofMinutes(5);

    public static VerificationData createNew(String code) {
        return new VerificationData(code, LocalDateTime.now().plus(CODE_VALID_DURATION), false);
    }

    public VerificationData(String code) {
        this(code, LocalDateTime.now().plus(CODE_VALID_DURATION), false);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public VerificationData withVerified() {
        return new VerificationData(this.code, this.expiryTime, true);
    }
}

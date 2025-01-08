package com.mycom.socket.auth.service.data;

import java.time.LocalDateTime;

import java.time.Duration;

public record VerificationData(String code, LocalDateTime expiryTime) {

    private static final Duration CODE_VALID_DURATION = Duration.ofMinutes(5);

    public VerificationData(String code) {
        this(code, LocalDateTime.now().plus(CODE_VALID_DURATION));
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}

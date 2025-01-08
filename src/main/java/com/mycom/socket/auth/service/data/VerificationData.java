package com.mycom.socket.auth.service.data;

import java.time.LocalDateTime;

public record VerificationData(
    int code,
    LocalDateTime expiryTime
) {}

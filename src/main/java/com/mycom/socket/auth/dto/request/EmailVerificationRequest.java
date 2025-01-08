package com.mycom.socket.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationRequest(
        @NotEmpty(message = "이메일 주소를 입력해주세요.")
        @Email(message = "유효하지 않은 이메일 형식입니다.")
        String email,
        @NotEmpty(message = "인증 코드를 입력해주세요.")
        @Pattern(regexp = "^[0-9]{6}$", message = "인증 코드는 6자리 숫자여야 합니다.")
        String code
) {
}
package com.mycom.socket.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record EmailRequest(
        @NotEmpty(message = "이메일 주소를 입력해주세요.")
        @Email(message = "유효하지 않은 이메일 형식입니다.")
        String email
) {
}

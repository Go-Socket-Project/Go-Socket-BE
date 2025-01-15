package com.mycom.socket.go_socket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다")
        String nickname,
        @Size(max = 100, message = "자기소개는 100자를 초과할 수 없습니다")
        String intro
) {}
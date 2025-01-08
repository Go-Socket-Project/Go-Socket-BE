package com.mycom.socket.auth.dto.response;

import com.mycom.socket.global.dto.ApiResponse;

public record EmailVerificationCheckResponseDto(ApiResponse<Boolean> apiResponse) {

    public static EmailVerificationCheckResponseDto createSuccessResponse() {
        return new EmailVerificationCheckResponseDto(ApiResponse.success("이메일 인증 성공", true));
    }

    public static EmailVerificationCheckResponseDto createFailureResponse(String errorMessage) {
        return new EmailVerificationCheckResponseDto(ApiResponse.error(errorMessage));
    }
}
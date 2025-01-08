package com.mycom.socket.auth.dto.response;

import com.mycom.socket.global.dto.ApiResponse;

public record EmailVerificationResponseDto(ApiResponse<String> apiResponse) {

    public static EmailVerificationResponseDto createSuccessResponse() {
        return new EmailVerificationResponseDto(ApiResponse.success("이메일 전송 성공"));
    }

    public static EmailVerificationResponseDto createFailureResponse(String errorMessage) {
        return new EmailVerificationResponseDto(ApiResponse.error(errorMessage));
    }
}
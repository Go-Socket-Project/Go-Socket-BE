package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.dto.request.EmailRequestDto;
import com.mycom.socket.auth.dto.request.EmailVerificationRequestDto;
import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.dto.response.EmailVerificationCheckResponseDto;
import com.mycom.socket.auth.dto.response.EmailVerificationResponseDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.auth.service.MailService;
import com.mycom.socket.global.exception.BaseException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MailService mailService;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request,
                                  HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }

    @PostMapping("/register")
    public Long register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/verification")
    public EmailVerificationResponseDto mailSend(@Valid @RequestBody EmailRequestDto emailRequestDto) {
        try {
            boolean isSuccess = mailService.sendMail(emailRequestDto.email());
            return isSuccess ? EmailVerificationResponseDto.createSuccessResponse() : EmailVerificationResponseDto.createFailureResponse("이메일 전송에 실패했습니다.");
        } catch (BaseException e) {
            return EmailVerificationResponseDto.createFailureResponse(e.getMessage());
        }
    }

    @GetMapping("/email/verify")
    public EmailVerificationCheckResponseDto mailCheck(@Valid @RequestBody EmailVerificationRequestDto emailRequestDto) {
        try {
            boolean isVerified =  mailService.verifyCode(emailRequestDto.email(), emailRequestDto.code());
            return isVerified ? EmailVerificationCheckResponseDto.createSuccessResponse() :
                    EmailVerificationCheckResponseDto.createFailureResponse("이메일 인증에 실패했습니다.");
        } catch (BaseException e) {
            return EmailVerificationCheckResponseDto.createFailureResponse(e.getMessage());
        }
    }

}

package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.dto.request.EmailRequest;
import com.mycom.socket.auth.dto.request.EmailVerificationRequest;
import com.mycom.socket.auth.dto.request.LoginRequest;
import com.mycom.socket.auth.dto.request.RegisterRequest;
import com.mycom.socket.auth.dto.response.EmailVerificationResponse;
import com.mycom.socket.auth.dto.response.LoginResponse;
import com.mycom.socket.auth.dto.response.RegisterResponse;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.auth.service.MailService;
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
    public LoginResponse login(@Valid @RequestBody LoginRequest request,
                               HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }

    @PostMapping("/verification")
    public EmailVerificationResponse sendVerificationEmail(@Valid @RequestBody EmailRequest request) {
        return mailService.sendMail(request.email());
    }

    @PostMapping("/email/verify")
    public EmailVerificationResponse verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        return mailService.verifyCode(request.email(), request.code());
    }

}

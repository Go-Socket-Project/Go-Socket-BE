package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
import com.mycom.socket.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}

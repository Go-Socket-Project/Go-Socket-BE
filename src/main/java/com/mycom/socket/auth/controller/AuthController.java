package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
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
    private int number;

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

    @PostMapping("/mail-send")
    public Integer mailSend(@RequestParam(name = "mail") String mail) {
        try {
            number = mailService.sendMail(mail);
            return number;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send mail: " + e.getMessage());
        }
    }

    @GetMapping("/mail-check")
    public Boolean mailCheck(@RequestParam(name = "userNumber") String userNumber) {
        return userNumber.equals(String.valueOf(number));
    }

}

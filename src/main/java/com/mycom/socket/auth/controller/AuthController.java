package com.mycom.socket.auth.controller;

import com.mycom.socket.auth.dto.request.LoginRequestDto;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.dto.response.LoginResponseDto;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.auth.service.MailService;
import com.mycom.socket.auth.service.RateLimiter;
import com.mycom.socket.global.exception.BaseException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MailService mailService;
    private final RateLimiter rateLimiter;

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

    @PostMapping("/email/verification")
    public Integer mailSend(@RequestParam(name = "mail") String mail) {
        try {
            rateLimiter.checkRateLimit(mail);  // 요청 제한 체크
            return mailService.sendMail(mail);
        } catch (Exception e) {
            throw new BaseException("이메일 전송에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/email/verify")
    public Boolean mailCheck(@RequestParam(name = "mail") String mail,
                             @RequestParam(name = "code") String code) {
        try {
            return mailService.verifyCode(mail, code);
        } catch (Exception e) {
            throw new BaseException("인증코드 검증에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
